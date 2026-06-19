package com.ruoyi.fishing.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.fishing.domain.FishMallCategory;
import com.ruoyi.fishing.domain.FishMallGoods;
import com.ruoyi.fishing.domain.FishMallOrder;
import com.ruoyi.fishing.domain.FishMallOrderItem;
import com.ruoyi.fishing.mapper.FishMallCategoryMapper;
import com.ruoyi.fishing.mapper.FishMallGoodsMapper;
import com.ruoyi.fishing.mapper.FishMallOrderMapper;
import com.ruoyi.fishing.service.IFishBalanceService;
import com.ruoyi.fishing.service.IFishUserService;
import com.ruoyi.fishing.domain.FishBalanceLog;
import com.ruoyi.fishing.service.IFishMallService;

@Service
public class FishMallServiceImpl implements IFishMallService
{
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(FishMallServiceImpl.class);

    @Autowired private FishMallCategoryMapper catMapper;
    @Autowired private FishMallGoodsMapper goodsMapper;
    @Autowired private FishMallOrderMapper orderMapper;
    @Autowired private IFishBalanceService balanceService;
    @Autowired private IFishUserService userService;
    @Autowired private com.ruoyi.fishing.service.IFishPointsService pointsService;

    // ===== 分类 =====
    @Override public List<FishMallCategory> listCategory(FishMallCategory q) { return catMapper.selectList(q); }
    @Override public FishMallCategory getCategory(Long catId) { return catMapper.selectById(catId); }
    @Override public int saveCategory(FishMallCategory c) { if (c.getStatus() == null) c.setStatus("0"); if (c.getSort() == null) c.setSort(0); return catMapper.insert(c); }
    @Override public int updateCategory(FishMallCategory c) { return catMapper.update(c); }
    @Override public int deleteCategories(Long[] ids) { return catMapper.deleteByIds(ids); }

    // ===== 商品 =====
    @Override public List<FishMallGoods> listGoods(FishMallGoods q) { return goodsMapper.selectList(q); }
    @Override public List<FishMallGoods> listActiveGoods(Long catId) { return goodsMapper.selectActive(catId); }
    @Override public FishMallGoods getGoods(Long goodsId) { return goodsMapper.selectById(goodsId); }
    @Override public int saveGoods(FishMallGoods g) { return goodsMapper.insert(g); }
    @Override public int updateGoods(FishMallGoods g) { return goodsMapper.update(g); }
    @Override public int toggleGoodsStatus(Long goodsId, String status) { return goodsMapper.updateStatus(goodsId, status); }
    @Override public int deleteGoods(Long[] ids) { return goodsMapper.deleteByIds(ids); }

    // ===== 订单 =====
    @Override
    @Transactional
    public FishMallOrder submitOrder(Long userId, List<Map<String, Object>> items, String remark, Long venueId)
    {
        return submitOrder(userId, items, remark, venueId, false, 0);
    }

    @Override
    @Transactional
    public FishMallOrder submitOrder(Long userId, List<Map<String, Object>> items, String remark, Long venueId, boolean useBalance)
    {
        return submitOrder(userId, items, remark, venueId, useBalance, 0);
    }

    @Override
    @Transactional
    public FishMallOrder submitOrder(Long userId, List<Map<String, Object>> items, String remark, Long venueId, boolean useBalance, int pointsToUse)
    {
        userService.assertNotBlacklisted(userId);
        if (items == null || items.isEmpty()) throw new ServiceException("商品清单为空");

        int total = 0;
        List<FishMallOrderItem> snapshots = new ArrayList<>();
        for (Map<String, Object> raw : items)
        {
            Long goodsId = toLong(raw.get("goodsId"));
            Integer qty  = toInt(raw.get("qty"));
            if (goodsId == null || qty == null || qty <= 0) throw new ServiceException("商品参数错误");

            FishMallGoods g = goodsMapper.selectById(goodsId);
            if (g == null) throw new ServiceException("商品不存在: " + goodsId);
            if (!"0".equals(g.getStatus())) throw new ServiceException("商品已下架: " + g.getName());

            int dec = goodsMapper.decreaseStock(goodsId, qty);
            if (dec == 0) throw new ServiceException("库存不足: " + g.getName());

            FishMallOrderItem item = new FishMallOrderItem();
            item.setGoodsId(goodsId);
            item.setName(g.getName());
            item.setSubtitle(g.getSubtitle());
            item.setCover(g.getCover());
            item.setPriceCents(g.getPriceCents());
            item.setQty(qty);
            snapshots.add(item);

            total += g.getPriceCents() * qty;
        }

        // 积分抵现预计算（优先级最高：先抵积分；100 积分 = 1 元，即 1 积分 = 1 分，可全额抵）
        int pointsUsed = 0;
        int pointsDeductCents = 0;
        if (pointsToUse > 0 && total > 0)
        {
            try
            {
                int userPoints = pointsService.getUserPoints(userId);
                int maxUsable = Math.min(userPoints, total); // 1 积分 = 1 分，最多抵到订单总额
                pointsUsed = Math.min(pointsToUse, maxUsable);
                if (pointsUsed < 0) pointsUsed = 0;
                pointsDeductCents = pointsUsed; // 1 积分 = 1 分
            }
            catch (Exception e)
            {
                log.warn("读取用户积分失败 userId={} 跳过积分抵扣: {}", userId, e.getMessage());
            }
        }
        int afterPoints = total - pointsDeductCents;

        // 余额抵扣预计算（积分之后，对剩余金额抵扣）
        int balanceCents = 0;
        if (useBalance && afterPoints > 0)
        {
            try
            {
                com.ruoyi.fishing.domain.FishUserBalance bal = balanceService.getBalance(userId);
                int avail = bal == null || bal.getBalanceCents() == null ? 0 : bal.getBalanceCents();
                balanceCents = Math.min(avail, afterPoints);
            }
            catch (Exception e)
            {
                log.warn("读取用户余额失败 userId={} 跳过抵扣: {}", userId, e.getMessage());
            }
        }

        long now = System.currentTimeMillis();
        FishMallOrder order = new FishMallOrder();
        order.setMallOrderNo("M" + now + String.format("%04d", ThreadLocalRandom.current().nextInt(10000)));
        order.setUserId(userId);
        order.setVenueId(venueId);
        order.setTotalCents(total);
        order.setAmountPaid(0);
        order.setBalanceCents(balanceCents);
        order.setPointsUsed(pointsUsed);
        order.setPointsDeductCents(pointsDeductCents);
        order.setStatus(0);
        order.setRemark2(remark == null ? "" : remark);
        order.setRedeemCode("");
        orderMapper.insert(order);

        for (FishMallOrderItem it : snapshots)
        {
            it.setMallOrderId(order.getMallOrderId());
            orderMapper.insertItem(it);
            goodsMapper.increaseSales(it.getGoodsId(), it.getQty());
        }
        order.setItems(snapshots);
        return order;
    }

    @Override
    @Transactional
    public FishMallOrder markPaid(String orderNo, String tradeNo)
    {
        FishMallOrder order = orderMapper.selectByOrderNo(orderNo);
        if (order == null) return null;
        if (order.getStatus() != null && order.getStatus() == 1) return order; // 幂等
        if (order.getStatus() == null || order.getStatus() != 0) return order;
        int g = orderMapper.updateStatusWithGuard(order.getMallOrderId(), 0, 1);
        if (g == 0) return orderMapper.selectById(order.getMallOrderId());

        int total = order.getTotalCents() == null ? 0 : order.getTotalCents();
        int balance = order.getBalanceCents() == null ? 0 : order.getBalanceCents();
        int points = order.getPointsUsed() == null ? 0 : order.getPointsUsed();
        int pointsCents = order.getPointsDeductCents() == null ? 0 : order.getPointsDeductCents();
        // 真正扣减积分（如有）
        if (points > 0)
        {
            pointsService.addPoints(order.getUserId(), -points, "mall",
                    order.getMallOrderNo(), "商城订单积分抵扣");
        }
        // 真正扣减余额（如有）
        if (balance > 0)
        {
            balanceService.applyDelta(order.getUserId(), -balance, FishBalanceLog.TYPE_CONSUME_MALL,
                    order.getMallOrderNo(), "商城订单抵扣", "system");
        }
        order.setStatus(1);
        order.setPaidTime(new Date());
        order.setAmountPaid(Math.max(0, total - pointsCents - balance));
        order.setBalanceCents(balance);
        order.setPointsUsed(points);
        order.setPointsDeductCents(pointsCents);
        order.setPayTradeNo(tradeNo == null ? "" : tradeNo);
        orderMapper.update(order);
        return order;
    }

    @Override
    @Transactional
    public FishMallOrder redeem(String orderNoOrCode, String operator)
    {
        if (orderNoOrCode == null || orderNoOrCode.isEmpty()) throw new ServiceException("请输入订单号");
        FishMallOrder order = orderMapper.selectByOrderNo(orderNoOrCode);
        if (order == null) order = orderMapper.selectByRedeemCode(orderNoOrCode);
        if (order == null) throw new ServiceException("订单不存在");
        if (order.getStatus() == null) throw new ServiceException("订单状态异常");
        if (order.getStatus() == 2) throw new ServiceException("该订单已领取");
        if (order.getStatus() != 1) throw new ServiceException("仅已支付未领取订单可确认领取");

        int g = orderMapper.updateStatusWithGuard(order.getMallOrderId(), 1, 2);
        if (g == 0) throw new ServiceException("订单状态已变更，请刷新");

        order.setStatus(2);
        order.setRedeemedTime(new Date());
        order.setRedeemedBy(operator == null ? "" : operator);
        orderMapper.update(order);
        return order;
    }

    @Override
    @Transactional
    public FishMallOrder cancel(Long mallOrderId)
    {
        FishMallOrder order = orderMapper.selectById(mallOrderId);
        if (order == null) throw new ServiceException("订单不存在");
        if (order.getStatus() == null || order.getStatus() != 0) throw new ServiceException("仅待支付订单可取消");

        int g = orderMapper.updateStatusWithGuard(mallOrderId, 0, 3);
        if (g == 0) throw new ServiceException("订单状态已变更");
        // 回滚库存
        List<FishMallOrderItem> items = orderMapper.selectItemsByOrderId(mallOrderId);
        for (FishMallOrderItem it : items) goodsMapper.increaseStock(it.getGoodsId(), it.getQty());
        order.setStatus(3);
        orderMapper.update(order);
        return order;
    }

    @Override
    public int autoCancelTimeoutOrders(int timeoutMinutes)
    {
        int minutes = timeoutMinutes <= 0 ? 30 : timeoutMinutes;
        Date threshold = new Date(System.currentTimeMillis() - minutes * 60_000L);
        List<FishMallOrder> list = orderMapper.selectTimeoutUnpaidOrders(threshold);
        int canceled = 0;
        for (FishMallOrder order : list)
        {
            try
            {
                // 每单独立事务：失败一个不影响其他
                FishMallOrder r = cancelSingleInNewTx(order.getMallOrderId());
                if (r != null) canceled++;
            }
            catch (Exception ignore) { }
        }
        return canceled;
    }

    /**
     * 独立事务取消单个订单（配合 autoCancelTimeoutOrders 使用，失败不影响批次）。
     * 通过注入 self bean 调用，以触发 Spring AOP 事务；这里直接调用 cancel 即可因为外层方法无 @Transactional。
     */
    private FishMallOrder cancelSingleInNewTx(Long mallOrderId)
    {
        FishMallOrder order = orderMapper.selectById(mallOrderId);
        if (order == null) return null;
        if (order.getStatus() == null || order.getStatus() != 0) return null;
        return cancel(mallOrderId);
    }

    @Override
    public List<FishMallOrder> listOrder(FishMallOrder q)
    {
        List<FishMallOrder> list = orderMapper.selectList(q);
        for (FishMallOrder o : list) o.setItems(orderMapper.selectItemsByOrderId(o.getMallOrderId()));
        return list;
    }

    @Override
    public FishMallOrder getOrder(Long mallOrderId)
    {
        FishMallOrder o = orderMapper.selectById(mallOrderId);
        if (o != null) o.setItems(orderMapper.selectItemsByOrderId(mallOrderId));
        return o;
    }

    @Override
    public List<FishMallOrder> listMyOrders(Long userId)
    {
        List<FishMallOrder> list = orderMapper.selectByUser(userId);
        for (FishMallOrder o : list) o.setItems(orderMapper.selectItemsByOrderId(o.getMallOrderId()));
        return list;
    }

    private String genRedeemCode()
    {
        // 8 位数字
        return String.valueOf(10000000L + ThreadLocalRandom.current().nextInt(89999999));
    }

    private Long toLong(Object v)
    {
        if (v == null) return null;
        if (v instanceof Number) return ((Number) v).longValue();
        try { return Long.parseLong(String.valueOf(v)); } catch (Exception e) { return null; }
    }

    private Integer toInt(Object v)
    {
        if (v == null) return null;
        if (v instanceof Number) return ((Number) v).intValue();
        try { return Integer.parseInt(String.valueOf(v)); } catch (Exception e) { return null; }
    }
}
