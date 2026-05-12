package com.ruoyi.fishing.service.impl;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.fishing.domain.FishBillingRule;
import com.ruoyi.fishing.domain.FishMallOrder;
import com.ruoyi.fishing.domain.FishOrder;
import com.ruoyi.fishing.domain.FishUserCoupon;
import com.ruoyi.fishing.domain.FishVenue;
import com.ruoyi.fishing.mapper.FishBillingRuleMapper;
import com.ruoyi.fishing.mapper.FishMallOrderMapper;
import com.ruoyi.fishing.mapper.FishOrderMapper;
import com.ruoyi.fishing.mapper.FishUserCouponMapper;
import com.ruoyi.fishing.mapper.FishVenueMapper;
import com.ruoyi.fishing.service.IFishOrderService;
import com.ruoyi.fishing.service.IFishBalanceService;
import com.ruoyi.fishing.service.IFishUserService;
import com.ruoyi.fishing.domain.FishBalanceLog;
import com.ruoyi.fishing.service.IFishMemberLevelService;
import com.ruoyi.fishing.service.IFishPointsService;
import com.ruoyi.fishing.util.BillingCalculator;

@Service
public class FishOrderServiceImpl implements IFishOrderService
{
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(FishOrderServiceImpl.class);
    private static final AtomicLong ORDER_SEQ = new AtomicLong(0);

    @Autowired
    private FishOrderMapper orderMapper;

    @Autowired
    private FishVenueMapper venueMapper;

    @Autowired
    private FishBillingRuleMapper ruleMapper;

    @Autowired
    private FishUserCouponMapper couponMapper;

    @Autowired
    private FishMallOrderMapper mallOrderMapper;

    @Autowired
    private IFishBalanceService balanceService;

    @Autowired
    private IFishUserService userService;

    @SuppressWarnings("rawtypes")
    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private IFishPointsService pointsService;

    @Autowired
    private IFishMemberLevelService memberLevelService;

    @Override
    public FishOrder selectFishOrderByOrderId(Long orderId) { return orderMapper.selectFishOrderByOrderId(orderId); }

    @Override
    public List<FishOrder> selectFishOrderList(FishOrder order) { return orderMapper.selectFishOrderList(order); }

    @Override
    public List<FishOrder> selectOrdersByUser(Long userId) { return orderMapper.selectOrdersByUser(userId); }

    @Override
    public FishOrder selectRunningOrder(Long userId) { return orderMapper.selectRunningOrder(userId); }

    @Override
    public FishOrder selectPendingOrder(Long userId) { return orderMapper.selectPendingOrder(userId); }

    @Override
    @Transactional
    @SuppressWarnings("unchecked")
    public FishOrder startOrder(Long userId, Long venueId)
    {
        userService.assertNotBlacklisted(userId);

        // Redis 分布式锁：同一用户 5 秒内不能重复发起
        String lockKey = "fishing:start_lock:" + userId;
        Boolean acquired = redisTemplate.opsForValue().setIfAbsent(lockKey, "1", 5, TimeUnit.SECONDS);
        if (!Boolean.TRUE.equals(acquired)) throw new ServiceException("操作过于频繁，请稍后再试");

        try {
            FishOrder pending = orderMapper.selectPendingOrder(userId);
            if (pending != null) throw new ServiceException("存在未支付订单，请先完成支付");

            // FOR UPDATE 行锁：防止并发插入重复 running 订单
            FishOrder running = orderMapper.selectRunningOrderForUpdate(userId);
            if (running != null) return running;

            FishVenue venue = venueMapper.selectFishVenueByVenueId(venueId);
            if (venue == null) throw new ServiceException("钓场不存在");
            if ("1".equals(venue.getStatus())) throw new ServiceException("钓场已停用");

            FishBillingRule rule = venue.getRuleId() != null ? ruleMapper.selectFishBillingRuleByRuleId(venue.getRuleId()) : null;
            if (rule == null) throw new ServiceException("未配置计费规则");

            Date now = new Date();
            FishOrder o = new FishOrder();
            o.setOrderNo("FD" + now.getTime() + String.format("%06d", ORDER_SEQ.incrementAndGet() % 1000000));
            o.setUserId(userId);
            o.setVenueId(venueId);
            o.setStatus(1);
            o.setStartTime(now);
            o.setDurationSeconds(0);
            o.setElapsedSeconds(0);
            o.setAmountCents(0);
            o.setDiscountCents(0);
            o.setRuleSnapshot(buildRuleSnapshot(rule));
            orderMapper.insertFishOrder(o);
            return o;
        } finally {
            redisTemplate.delete(lockKey);
        }
    }

    @Override
    public FishOrder estimateRunning(Long userId)
    {
        FishOrder running = orderMapper.selectRunningOrder(userId);
        if (running == null) return null;
        FishBillingRule rule = loadRuleForOrder(running);
        BillingCalculator.Result r = BillingCalculator.calc(rule, running.getStartTime().getTime(), System.currentTimeMillis());
        running.setElapsedSeconds(r.elapsedSeconds);
        running.setDurationSeconds(r.billableDurationSeconds);
        running.setAmountCents(r.amountCents);
        return running;
    }

    @Override
    @Transactional
    public FishOrder finishOrder(Long userId)
    {
        FishOrder running = orderMapper.selectRunningOrder(userId);
        if (running == null) throw new ServiceException("未检测到进行中的订单");

        FishBillingRule rule = loadRuleForOrder(running);
        Date now = new Date();
        BillingCalculator.Result r = BillingCalculator.calc(rule, running.getStartTime().getTime(), now.getTime());

        running.setStatus(2);
        running.setEndTime(now);
        running.setElapsedSeconds(r.elapsedSeconds);
        running.setDurationSeconds(r.billableDurationSeconds);
        running.setAmountCents(r.amountCents);
        orderMapper.updateFishOrder(running);
        return running;
    }

    @Override
    @Transactional
    public FishOrder pay(Long userId, Long orderId, Long couponId, java.util.List<Long> mallOrderIds)
    {
        return pay(userId, orderId, couponId, mallOrderIds, false);
    }

    @Override
    @Transactional
    public FishOrder pay(Long userId, Long orderId, Long couponId, java.util.List<Long> mallOrderIds, boolean useBalance)
    {
        FishOrder order = orderMapper.selectFishOrderByOrderId(orderId);
        if (order == null) throw new ServiceException("订单不存在");
        if (!order.getUserId().equals(userId)) throw new ServiceException("订单不属于当前用户");
        if (order.getStatus() == 3) return order; // 幂等：已完成直接返回
        if (order.getStatus() != 2 && order.getStatus() != 0) throw new ServiceException("订单状态不允许支付");

        PaymentPlan plan = computePaymentPlan(order, userId, couponId, mallOrderIds, useBalance);

        int expected = order.getStatus();
        int guarded = orderMapper.updateOrderStatusWithGuard(orderId, expected, 3);
        if (guarded == 0) throw new ServiceException("订单状态已变更，请刷新后重试");

        // 扣减余额（如有）
        if (plan.balanceCents > 0)
        {
            try
            {
                balanceService.applyDelta(userId, -plan.balanceCents, FishBalanceLog.TYPE_CONSUME_FISHING,
                        order.getOrderNo(), "钓场订单抵扣", "system");
            }
            catch (Exception e)
            {
                // 余额扣减失败：回滚状态并抛出
                orderMapper.updateOrderStatusWithGuard(orderId, 3, expected);
                throw new ServiceException("余额扣减失败：" + e.getMessage());
            }
        }

        order.setStatus(3);
        order.setDiscountCents(plan.discountCents);
        order.setBalanceCents(plan.balanceCents);
        order.setAmountPaid(plan.wxAmountCents);
        order.setPaidTime(new Date());
        if (order.getPayTradeNo() == null || order.getPayTradeNo().isEmpty())
        {
            order.setPayTradeNo("MOCK" + System.currentTimeMillis());
        }
        orderMapper.updateFishOrder(order);
        settleAttachedMallOrders(order, order.getPayTradeNo());
        return order;
    }

    @Override
    @Transactional
    public FishOrder preparePayment(Long userId, Long orderId, Long couponId, java.util.List<Long> mallOrderIds)
    {
        return preparePayment(userId, orderId, couponId, mallOrderIds, false);
    }

    @Override
    @Transactional
    public FishOrder preparePayment(Long userId, Long orderId, Long couponId, java.util.List<Long> mallOrderIds, boolean useBalance)
    {
        FishOrder order = orderMapper.selectFishOrderByOrderId(orderId);
        if (order == null) throw new ServiceException("订单不存在");
        if (!order.getUserId().equals(userId)) throw new ServiceException("订单不属于当前用户");
        if (order.getStatus() == 3) return order;
        if (order.getStatus() != 2 && order.getStatus() != 0) throw new ServiceException("订单状态不允许支付");

        PaymentPlan plan = computePaymentPlan(order, userId, couponId, mallOrderIds, useBalance);
        // 不实际扣余额（预支付阶段）。amountPaid 只存微信需付部分，balanceCents 记录拟抵扣金额。
        order.setDiscountCents(plan.discountCents);
        order.setBalanceCents(plan.balanceCents);
        order.setAmountPaid(plan.wxAmountCents);
        orderMapper.updateFishOrder(order);
        return order;
    }

    /**
     * 计算支付方案。会在内部调用优惠券核销 + 附加商城单。
     * 顺序：应付金额 → 优惠券抵扣 → 加商城单金额 → 余额抵扣 → 微信需付。
     */
    private PaymentPlan computePaymentPlan(FishOrder order, Long userId, Long couponId,
                                           java.util.List<Long> mallOrderIds, boolean useBalance)
    {
        int finalAmount = order.getAmountCents() == null ? 0 : order.getAmountCents();
        int discount = 0;
        if (couponId != null)
        {
            FishUserCoupon coupon = couponMapper.selectFishUserCouponByCouponId(couponId);
            if (coupon == null || !coupon.getUserId().equals(userId)) throw new ServiceException("优惠券不存在");
            if (coupon.getUsed() != null && coupon.getUsed() == 1) throw new ServiceException("优惠券已使用");
            if (coupon.getExpireTime() != null && coupon.getExpireTime().before(new Date())) throw new ServiceException("优惠券已过期");

            if ("amount".equals(coupon.getCouponType()))
            {
                int min = coupon.getMinAmountCents() == null ? 0 : coupon.getMinAmountCents();
                if (finalAmount >= min)
                {
                    discount = Math.min(coupon.getCouponValue(), finalAmount);
                    finalAmount -= discount;
                }
            }
            else if ("duration".equals(coupon.getCouponType()))
            {
                FishBillingRule rule = loadRuleForOrder(order);
                int stepMinutes = rule.getStepMinutes() == null ? 30 : rule.getStepMinutes();
                int pricePerStep = rule.getPricePerStepCents() == null ? 0 : rule.getPricePerStepCents();
                int freeMinutes = coupon.getCouponValue() == null ? 0 : coupon.getCouponValue();
                int freeSteps = freeMinutes / stepMinutes;
                discount = Math.min(freeSteps * pricePerStep, finalAmount);
                finalAmount -= discount;
            }
            int used = couponMapper.useCoupon(couponId, order.getOrderId());
            if (used == 0) throw new ServiceException("优惠券已被使用");
            order.setCouponId(couponId);
        }

        // 合并支付：累加附带商城订单金额
        int mallTotal = attachAndSumMallOrders(order, userId, mallOrderIds);
        finalAmount += mallTotal;

        // 余额抵扣（仅查询余额，不做扣减；扣减延到 pay 成功后）
        int balanceUsed = 0;
        if (useBalance && finalAmount > 0)
        {
            try
            {
                com.ruoyi.fishing.domain.FishUserBalance bal = balanceService.getBalance(userId);
                int avail = bal == null || bal.getBalanceCents() == null ? 0 : bal.getBalanceCents();
                balanceUsed = Math.min(avail, finalAmount);
                finalAmount -= balanceUsed;
            }
            catch (Exception e)
            {
                log.warn("读取用户余额失败 userId={}, 跳过余额抵扣: {}", userId, e.getMessage());
            }
        }

        PaymentPlan plan = new PaymentPlan();
        plan.discountCents = discount;
        plan.balanceCents = balanceUsed;
        plan.wxAmountCents = finalAmount;
        return plan;
    }

    private static class PaymentPlan
    {
        int discountCents;
        int balanceCents;
        int wxAmountCents;
    }

    @Override
    @Transactional
    public FishOrder markPaid(String orderNo, String tradeNo)
    {
        FishOrder order = orderMapper.selectFishOrderByOrderNo(orderNo);
        if (order == null) return null;
        if (order.getStatus() == 3) return order;
        orderMapper.updateOrderStatusWithGuard(order.getOrderId(), order.getStatus(), 3);
        order.setStatus(3);
        order.setPaidTime(new Date());
        // 优先信任 preparePayment 写入的 amountPaid（含商城金额，不含余额抵扣）
        if (order.getAmountPaid() == null || order.getAmountPaid() <= 0)
        {
            int amount = order.getAmountCents() == null ? 0 : order.getAmountCents();
            int discount = order.getDiscountCents() == null ? 0 : order.getDiscountCents();
            int balance  = order.getBalanceCents() == null ? 0 : order.getBalanceCents();
            order.setAmountPaid(Math.max(0, amount - discount - balance));
        }
        order.setPayTradeNo(tradeNo);
        // 微信已支付到账：真正扣减余额
        int balanceToDeduct = order.getBalanceCents() == null ? 0 : order.getBalanceCents();
        if (balanceToDeduct > 0)
        {
            try
            {
                balanceService.applyDelta(order.getUserId(), -balanceToDeduct, FishBalanceLog.TYPE_CONSUME_FISHING,
                        order.getOrderNo(), "钓场订单抵扣", "system");
            }
            catch (Exception e)
            {
                // 余额不足时降级：记录日志，不阻塞订单完成（preparePayment 时余额已校验）
                log.error("钓场订单余额扣减失败 orderNo={} balance={} err={}", orderNo, balanceToDeduct, e.getMessage());
                order.setBalanceCents(0);
                order.setAmountPaid(order.getAmountPaid() + balanceToDeduct);
            }
        }
        orderMapper.updateFishOrder(order);
        // 推进合并支付的商城订单
        settleAttachedMallOrders(order, tradeNo);
        // 消费赠积分 + 刷新会员等级
        try {
            int totalPaid = (order.getAmountPaid() == null ? 0 : order.getAmountPaid()) + balanceToDeduct;
            if (totalPaid > 0) pointsService.grantConsumePoints(order.getUserId(), totalPaid, order.getOrderNo());
            memberLevelService.refreshUserLevel(order.getUserId());
        } catch (Exception e) {
            log.warn("积分/等级刷新异常 orderNo={} err={}", orderNo, e.getMessage());
        }
        return order;
    }

    @Override
    @Transactional
    public FishOrder adminFinish(Long orderId)
    {
        FishOrder order = orderMapper.selectFishOrderByOrderId(orderId);
        if (order == null) throw new ServiceException("订单不存在");
        if (order.getStatus() != 1) throw new ServiceException("仅计时中订单可人工结束");

        FishBillingRule rule = loadRuleForOrder(order);
        Date now = new Date();
        BillingCalculator.Result r = BillingCalculator.calc(rule, order.getStartTime().getTime(), now.getTime());
        order.setStatus(2);
        order.setEndTime(now);
        order.setElapsedSeconds(r.elapsedSeconds);
        order.setDurationSeconds(r.billableDurationSeconds);
        order.setAmountCents(r.amountCents);
        orderMapper.updateFishOrder(order);
        return order;
    }

    @Override
    @Transactional
    public int adminCancel(Long orderId, String reason)
    {
        FishOrder order = orderMapper.selectFishOrderByOrderId(orderId);
        if (order == null) throw new ServiceException("订单不存在");
        if (order.getStatus() == 3) throw new ServiceException("已完成订单不可取消");
        order.setStatus(4);
        order.setCancelReason(reason == null ? "" : reason);
        return orderMapper.updateFishOrder(order);
    }

    @Override
    public int autoSettleTimeoutOrders(int timeoutHours)
    {
        int hours = timeoutHours <= 0 ? 24 : timeoutHours;
        Date threshold = new Date(System.currentTimeMillis() - hours * 3600_000L);
        List<FishOrder> list = orderMapper.selectTimeoutRunningOrders(threshold);
        int settled = 0;
        for (FishOrder order : list)
        {
            try
            {
                FishBillingRule rule = loadRuleForOrder(order);
                Date now = new Date();
                BillingCalculator.Result r = BillingCalculator.calc(rule, order.getStartTime().getTime(), now.getTime());
                int guarded = orderMapper.updateOrderStatusWithGuard(order.getOrderId(), 1, 2);
                if (guarded == 0) continue;
                order.setStatus(2);
                order.setEndTime(now);
                order.setElapsedSeconds(r.elapsedSeconds);
                order.setDurationSeconds(r.billableDurationSeconds);
                order.setAmountCents(r.amountCents);
                orderMapper.updateFishOrder(order);
                settled++;
            }
            catch (Exception ignore) { }
        }
        return settled;
    }

    @Override
    public int deleteFishOrderByOrderIds(Long[] orderIds) { return orderMapper.deleteFishOrderByOrderIds(orderIds); }

    /** 校验并累加附带商城订单：写入 order.mallOrderIds（CSV），返回总金额（分）。空列表则清空 CSV。 */
    private int attachAndSumMallOrders(FishOrder order, Long userId, java.util.List<Long> mallOrderIds)
    {
        int total = 0;
        StringBuilder ids = new StringBuilder();
        if (mallOrderIds != null)
        {
            for (Long mid : mallOrderIds)
            {
                if (mid == null) continue;
                FishMallOrder m = mallOrderMapper.selectById(mid);
                if (m == null) throw new ServiceException("商城订单不存在: " + mid);
                if (!userId.equals(m.getUserId())) throw new ServiceException("商城订单不属于当前用户");
                if (m.getStatus() == null || m.getStatus() != 0) throw new ServiceException("商城订单已支付或取消");
                total += m.getTotalCents() == null ? 0 : m.getTotalCents();
                if (ids.length() > 0) ids.append(",");
                ids.append(mid);
            }
        }
        order.setMallOrderIds(ids.toString());
        return total;
    }

    /** 把 order.mallOrderIds 关联的商城订单一并标记为已支付（status 0→1）。幂等。 */
    private void settleAttachedMallOrders(FishOrder order, String tradeNo)
    {
        String ids = order.getMallOrderIds();
        if (ids == null || ids.isEmpty()) return;
        for (String idStr : ids.split(","))
        {
            String t = idStr.trim();
            if (t.isEmpty()) continue;
            try
            {
                Long mid = Long.parseLong(t);
                FishMallOrder m = mallOrderMapper.selectById(mid);
                if (m == null) continue;
                if (m.getStatus() != null && m.getStatus() == 1) continue; // 幂等
                if (m.getStatus() == null || m.getStatus() != 0) continue;
                int g = mallOrderMapper.updateStatusWithGuard(mid, 0, 1);
                if (g == 0) continue;
                m.setStatus(1);
                m.setPaidTime(new Date());
                m.setAmountPaid(m.getTotalCents());
                m.setPayTradeNo(tradeNo == null ? "" : tradeNo);
                mallOrderMapper.update(m);
            }
            catch (NumberFormatException ignore) { }
        }
    }

    private FishBillingRule loadRuleForOrder(FishOrder order)
    {
        FishVenue venue = venueMapper.selectFishVenueByVenueId(order.getVenueId());
        if (venue == null || venue.getRuleId() == null) throw new ServiceException("计费规则缺失");
        FishBillingRule rule = ruleMapper.selectFishBillingRuleByRuleId(venue.getRuleId());
        if (rule == null) throw new ServiceException("计费规则不存在");
        return rule;
    }

    private String buildRuleSnapshot(FishBillingRule rule)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"ruleId\":").append(rule.getRuleId()).append(",");
        sb.append("\"ruleName\":\"").append(rule.getRuleName()).append("\",");
        sb.append("\"stepMinutes\":").append(rule.getStepMinutes()).append(",");
        sb.append("\"pricePerStepCents\":").append(rule.getPricePerStepCents()).append(",");
        sb.append("\"minDurationMinutes\":").append(rule.getMinDurationMinutes()).append(",");
        sb.append("\"capAmountCents\":").append(rule.getCapAmountCents());
        sb.append("}");
        return sb.toString();
    }
}
