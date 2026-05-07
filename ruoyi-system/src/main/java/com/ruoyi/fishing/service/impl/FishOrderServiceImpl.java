package com.ruoyi.fishing.service.impl;

import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.fishing.domain.FishBillingRule;
import com.ruoyi.fishing.domain.FishOrder;
import com.ruoyi.fishing.domain.FishUserCoupon;
import com.ruoyi.fishing.domain.FishVenue;
import com.ruoyi.fishing.mapper.FishBillingRuleMapper;
import com.ruoyi.fishing.mapper.FishOrderMapper;
import com.ruoyi.fishing.mapper.FishUserCouponMapper;
import com.ruoyi.fishing.mapper.FishVenueMapper;
import com.ruoyi.fishing.service.IFishOrderService;
import com.ruoyi.fishing.util.BillingCalculator;

@Service
public class FishOrderServiceImpl implements IFishOrderService
{
    @Autowired
    private FishOrderMapper orderMapper;

    @Autowired
    private FishVenueMapper venueMapper;

    @Autowired
    private FishBillingRuleMapper ruleMapper;

    @Autowired
    private FishUserCouponMapper couponMapper;

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
    public FishOrder startOrder(Long userId, Long venueId)
    {
        FishOrder pending = orderMapper.selectPendingOrder(userId);
        if (pending != null) throw new ServiceException("存在未支付订单，请先完成支付");

        FishOrder running = orderMapper.selectRunningOrder(userId);
        if (running != null) return running;

        FishVenue venue = venueMapper.selectFishVenueByVenueId(venueId);
        if (venue == null) throw new ServiceException("钓场不存在");
        if ("1".equals(venue.getStatus())) throw new ServiceException("钓场已停用");

        FishBillingRule rule = venue.getRuleId() != null ? ruleMapper.selectFishBillingRuleByRuleId(venue.getRuleId()) : null;
        if (rule == null) throw new ServiceException("未配置计费规则");

        Date now = new Date();
        FishOrder o = new FishOrder();
        o.setOrderNo("FD" + now.getTime());
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
    public FishOrder pay(Long userId, Long orderId, Long couponId)
    {
        FishOrder order = orderMapper.selectFishOrderByOrderId(orderId);
        if (order == null) throw new ServiceException("订单不存在");
        if (!order.getUserId().equals(userId)) throw new ServiceException("订单不属于当前用户");
        if (order.getStatus() == 3) return order; // 幂等：已完成直接返回
        if (order.getStatus() != 2 && order.getStatus() != 0) throw new ServiceException("订单状态不允许支付");

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
            int used = couponMapper.useCoupon(couponId, orderId);
            if (used == 0) throw new ServiceException("优惠券已被使用");
            order.setCouponId(couponId);
        }

        int expected = order.getStatus();
        int guarded = orderMapper.updateOrderStatusWithGuard(orderId, expected, 3);
        if (guarded == 0) throw new ServiceException("订单状态已变更，请刷新后重试");

        order.setStatus(3);
        order.setDiscountCents(discount);
        order.setAmountPaid(finalAmount);
        order.setPaidTime(new Date());
        if (order.getPayTradeNo() == null || order.getPayTradeNo().isEmpty())
        {
            order.setPayTradeNo("MOCK" + System.currentTimeMillis());
        }
        orderMapper.updateFishOrder(order);
        return order;
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
        order.setAmountPaid(order.getAmountCents() == null ? 0 : order.getAmountCents());
        order.setPayTradeNo(tradeNo);
        orderMapper.updateFishOrder(order);
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
    public int deleteFishOrderByOrderIds(Long[] orderIds) { return orderMapper.deleteFishOrderByOrderIds(orderIds); }

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
