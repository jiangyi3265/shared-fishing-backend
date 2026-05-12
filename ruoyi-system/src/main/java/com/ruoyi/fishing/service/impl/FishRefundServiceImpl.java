package com.ruoyi.fishing.service.impl;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.fishing.domain.FishBalanceLog;
import com.ruoyi.fishing.domain.FishMallOrder;
import com.ruoyi.fishing.domain.FishMallOrderItem;
import com.ruoyi.fishing.domain.FishOrder;
import com.ruoyi.fishing.domain.FishRefund;
import com.ruoyi.fishing.mapper.FishMallGoodsMapper;
import com.ruoyi.fishing.mapper.FishMallOrderMapper;
import com.ruoyi.fishing.mapper.FishOrderMapper;
import com.ruoyi.fishing.mapper.FishRefundMapper;
import com.ruoyi.fishing.service.IFishBalanceService;
import com.ruoyi.fishing.service.IFishRefundService;
import com.ruoyi.fishing.service.IWxPayService;

@Service
public class FishRefundServiceImpl implements IFishRefundService
{
    public static final String TYPE_FISHING = "fishing";
    public static final String TYPE_MALL = "mall";

    private static final Logger log = LoggerFactory.getLogger(FishRefundServiceImpl.class);

    @Autowired private FishRefundMapper refundMapper;
    @Autowired private FishOrderMapper orderMapper;
    @Autowired private FishMallOrderMapper mallOrderMapper;
    @Autowired private FishMallGoodsMapper mallGoodsMapper;
    @Autowired private IFishBalanceService balanceService;
    @Autowired private IWxPayService wxPayService;

    @Override
    public FishRefund selectFishRefundByRefundId(Long refundId) { return refundMapper.selectFishRefundByRefundId(refundId); }

    @Override
    public List<FishRefund> selectFishRefundList(FishRefund refund) { return refundMapper.selectFishRefundList(refund); }

    @Override
    public List<FishRefund> selectByUserId(Long userId) { return refundMapper.selectByUserId(userId); }

    @Override
    @Transactional
    public FishRefund applyRefund(Long userId, Long orderId, Integer applyAmountCents, String reason)
    {
        FishOrder order = orderMapper.selectFishOrderByOrderId(orderId);
        if (order == null) throw new ServiceException("订单不存在");
        if (!order.getUserId().equals(userId)) throw new ServiceException("订单不属于当前用户");
        if (order.getStatus() == null || order.getStatus() != 3) throw new ServiceException("仅已完成订单可申请退款");

        int paid = order.getAmountPaid() == null ? 0 : order.getAmountPaid();
        int balance = order.getBalanceCents() == null ? 0 : order.getBalanceCents();
        int refundable = paid + balance; // 余额抵扣部分也纳入可退总额
        if (refundable <= 0) throw new ServiceException("订单可退金额为 0");

        FishRefund active = refundMapper.selectActiveRefundByOrderId(orderId, TYPE_FISHING);
        if (active != null) throw new ServiceException("已有进行中的退款申请，请勿重复提交");

        int amount = applyAmountCents == null ? refundable : applyAmountCents;
        if (amount <= 0 || amount > refundable) throw new ServiceException("退款金额非法");

        FishRefund r = new FishRefund();
        r.setRefundNo("RF" + System.currentTimeMillis() + String.format("%04d", ThreadLocalRandom.current().nextInt(10000)));
        r.setOrderId(orderId);
        r.setOrderNo(order.getOrderNo());
        r.setOrderType(TYPE_FISHING);
        r.setUserId(userId);
        r.setApplyAmountCents(amount);
        r.setRefundAmountCents(0);
        r.setReason(reason == null ? "" : reason);
        r.setStatus(0);
        refundMapper.insertFishRefund(r);
        return r;
    }

    @Override
    @Transactional
    public FishRefund applyMallRefund(Long userId, Long mallOrderId, Integer applyAmountCents, String reason)
    {
        FishMallOrder order = mallOrderMapper.selectById(mallOrderId);
        if (order == null) throw new ServiceException("订单不存在");
        if (!userId.equals(order.getUserId())) throw new ServiceException("订单不属于当前用户");
        // 已核销后不再允许退款；待核销(1) 可申请
        if (order.getStatus() == null || order.getStatus() != 1) throw new ServiceException("仅待核销订单可申请退款");

        int paid = order.getAmountPaid() == null ? 0 : order.getAmountPaid();
        int balance = order.getBalanceCents() == null ? 0 : order.getBalanceCents();
        int total = order.getTotalCents() == null ? 0 : order.getTotalCents();
        int refundable = paid + balance;
        if (refundable <= 0) refundable = total; // 兜底
        if (refundable <= 0) throw new ServiceException("订单可退金额为 0");

        FishRefund active = refundMapper.selectActiveRefundByOrderId(mallOrderId, TYPE_MALL);
        if (active != null) throw new ServiceException("已有进行中的退款申请，请勿重复提交");

        int amount = applyAmountCents == null ? refundable : applyAmountCents;
        if (amount <= 0 || amount > refundable) throw new ServiceException("退款金额非法");

        FishRefund r = new FishRefund();
        r.setRefundNo("RF" + System.currentTimeMillis() + String.format("%04d", ThreadLocalRandom.current().nextInt(10000)));
        r.setOrderId(mallOrderId);
        r.setOrderNo(order.getMallOrderNo());
        r.setOrderType(TYPE_MALL);
        r.setUserId(userId);
        r.setApplyAmountCents(amount);
        r.setRefundAmountCents(0);
        r.setReason(reason == null ? "" : reason);
        r.setStatus(0);
        refundMapper.insertFishRefund(r);
        return r;
    }

    @Override
    @Transactional
    public FishRefund approve(Long refundId, Integer refundAmountCents, String remark, String operator)
    {
        FishRefund r = refundMapper.selectFishRefundByRefundId(refundId);
        if (r == null) throw new ServiceException("退款单不存在");
        if (r.getStatus() == null || r.getStatus() != 0) throw new ServiceException("仅待审核状态可通过");

        int amount = (refundAmountCents == null || refundAmountCents <= 0) ? r.getApplyAmountCents() : refundAmountCents;
        if (amount > r.getApplyAmountCents()) throw new ServiceException("实退金额不可大于申请金额");

        boolean isMall = TYPE_MALL.equals(r.getOrderType());

        // 0 -> 1 退款中
        int g = refundMapper.updateStatusWithGuard(refundId, 0, 1);
        if (g == 0) throw new ServiceException("状态已变更，请刷新");

        r.setStatus(1);
        r.setRefundAmountCents(amount);
        r.setAuditRemark(remark == null ? "" : remark);
        r.setAuditBy(operator);
        r.setAuditTime(new Date());

        // 取关联订单金额信息
        int balanceUsed;
        int totalPaid;
        if (isMall)
        {
            FishMallOrder m = mallOrderMapper.selectById(r.getOrderId());
            if (m == null) throw new ServiceException("商城订单不存在");
            totalPaid = m.getAmountPaid() == null ? 0 : m.getAmountPaid();
            balanceUsed = m.getBalanceCents() == null ? 0 : m.getBalanceCents();
        }
        else
        {
            FishOrder order = orderMapper.selectFishOrderByOrderId(r.getOrderId());
            totalPaid = order == null || order.getAmountPaid() == null ? 0 : order.getAmountPaid();
            balanceUsed = order == null || order.getBalanceCents() == null ? 0 : order.getBalanceCents();
        }

        // 退款金额优先还原到微信（已付现金），剩余退回余额
        int wxPart = Math.min(amount, totalPaid);
        int balancePart = Math.max(0, amount - wxPart);
        if (balancePart > balanceUsed) balancePart = balanceUsed; // 不允许超过实际抵扣的部分

        // 1) 退余额部分（先同步完成）
        if (balancePart > 0)
        {
            try
            {
                balanceService.applyDelta(r.getUserId(), balancePart, FishBalanceLog.TYPE_REFUND,
                        r.getOrderNo(), "退款返还余额 · " + r.getRefundNo(), operator);
            }
            catch (Exception e)
            {
                log.error("退款返还余额失败 refundNo={} err={}", r.getRefundNo(), e.getMessage());
                // 失败直接置为退款失败
                refundMapper.updateStatusWithGuard(refundId, 1, 4);
                r.setStatus(4);
                r.setAuditRemark((remark == null ? "" : remark) + " | 余额返还失败: " + e.getMessage());
                refundMapper.updateFishRefund(r);
                throw new ServiceException("余额返还失败：" + e.getMessage());
            }
        }

        // 2) 微信部分
        if (wxPart > 0 && wxPayService.isEnabled())
        {
            try
            {
                // totalCents 用订单实际微信实付金额
                String wxRefundNo = wxPayService.refund(r.getOrderNo(), r.getRefundNo(), wxPart, totalPaid, remark);
                r.setWxRefundNo(wxRefundNo == null ? "" : wxRefundNo);
                refundMapper.updateFishRefund(r);
                // 商城订单立即置已退款/已关闭（库存不回滚：已经预扣，核销前已支付）
                if (isMall) markMallRefundProcessing(r.getOrderId());
                return r; // 等待 wx 异步回调推进 2 / 4
            }
            catch (Exception e)
            {
                log.error("微信退款下单失败 refundNo={}", r.getRefundNo(), e);
                refundMapper.updateStatusWithGuard(refundId, 1, 4);
                r.setStatus(4);
                r.setAuditRemark((remark == null ? "" : remark) + " | 微信退款失败: " + e.getMessage());
                refundMapper.updateFishRefund(r);
                throw new ServiceException("微信退款发起失败：" + e.getMessage());
            }
        }

        // 3) mock 或 纯余额退：直接置完成
        if (wxPart > 0)
        {
            if (!wxPayService.isMockEnabled()) throw new ServiceException("微信支付未启用，无法发起退款");
            r.setWxRefundNo("MOCK_REFUND_" + System.currentTimeMillis());
        }
        refundMapper.updateStatusWithGuard(refundId, 1, 2);
        r.setStatus(2);
        r.setFinishTime(new Date());
        refundMapper.updateFishRefund(r);
        if (isMall) markMallRefundProcessing(r.getOrderId());
        return r;
    }

    /** 商城订单退款进入流程后，把订单从 1 待核销 标记为 3 已取消（避免继续核销） */
    private void markMallRefundProcessing(Long mallOrderId)
    {
        FishMallOrder m = mallOrderMapper.selectById(mallOrderId);
        if (m == null) return;
        if (m.getStatus() == null) return;
        if (m.getStatus() != 1) return;
        int g = mallOrderMapper.updateStatusWithGuard(mallOrderId, 1, 3);
        if (g == 0) return;
        // 回滚库存
        List<FishMallOrderItem> items = mallOrderMapper.selectItemsByOrderId(mallOrderId);
        for (FishMallOrderItem it : items) mallGoodsMapper.increaseStock(it.getGoodsId(), it.getQty());
        m.setStatus(3);
        mallOrderMapper.update(m);
    }

    @Override
    @Transactional
    public FishRefund reject(Long refundId, String remark, String operator)
    {
        FishRefund r = refundMapper.selectFishRefundByRefundId(refundId);
        if (r == null) throw new ServiceException("退款单不存在");
        int g = refundMapper.updateStatusWithGuard(refundId, 0, 3);
        if (g == 0) throw new ServiceException("仅待审核状态可驳回");
        r.setStatus(3);
        r.setAuditRemark(remark == null ? "" : remark);
        r.setAuditBy(operator);
        r.setAuditTime(new Date());
        refundMapper.updateFishRefund(r);
        return r;
    }

    @Override
    @Transactional
    public FishRefund handleRefundCallback(String refundNo, boolean success, String wxRefundNo)
    {
        FishRefund r = refundMapper.selectFishRefundByRefundNo(refundNo);
        if (r == null) return null;
        if (r.getStatus() != null && (r.getStatus() == 2 || r.getStatus() == 4)) return r; // 幂等
        int target = success ? 2 : 4;
        refundMapper.updateStatusWithGuard(r.getRefundId(), 1, target);
        r.setStatus(target);
        if (wxRefundNo != null && !wxRefundNo.isEmpty()) r.setWxRefundNo(wxRefundNo);
        if (success) r.setFinishTime(new Date());
        refundMapper.updateFishRefund(r);

        // 商城订单：退款失败则回滚到 1 待核销（让用户能继续核销/重新申请）；成功无操作（已是 3 取消）
        if (TYPE_MALL.equals(r.getOrderType()) && !success)
        {
            FishMallOrder m = mallOrderMapper.selectById(r.getOrderId());
            if (m != null && m.getStatus() != null && m.getStatus() == 3)
            {
                int g = mallOrderMapper.updateStatusWithGuard(r.getOrderId(), 3, 1);
                if (g > 0)
                {
                    // 恢复库存扣减
                    List<FishMallOrderItem> items = mallOrderMapper.selectItemsByOrderId(r.getOrderId());
                    for (FishMallOrderItem it : items)
                    {
                        int ok = mallGoodsMapper.decreaseStock(it.getGoodsId(), it.getQty());
                        if (ok == 0) log.warn("退款失败回滚库存失败 goodsId={} qty={}", it.getGoodsId(), it.getQty());
                    }
                }
            }
            // 退款失败：余额已经返还过，理论上要扣回；这里记日志由人工处理
            if (r.getRefundAmountCents() != null)
            {
                FishMallOrder mo = mallOrderMapper.selectById(r.getOrderId());
                int balanceUsed = mo == null || mo.getBalanceCents() == null ? 0 : mo.getBalanceCents();
                int wxPart = Math.min(r.getRefundAmountCents(), mo == null || mo.getAmountPaid() == null ? 0 : mo.getAmountPaid());
                int balancePart = Math.max(0, r.getRefundAmountCents() - wxPart);
                if (balancePart > balanceUsed) balancePart = balanceUsed;
                if (balancePart > 0)
                {
                    try
                    {
                        balanceService.applyDelta(r.getUserId(), -balancePart, FishBalanceLog.TYPE_REFUND,
                                r.getOrderNo(), "退款失败回扣 · " + r.getRefundNo(), "system");
                    }
                    catch (Exception e)
                    {
                        log.error("退款失败回扣余额失败 refundNo={} err={}", r.getRefundNo(), e.getMessage());
                    }
                }
            }
        }
        return r;
    }

    @Override
    public int deleteFishRefundByRefundIds(Long[] refundIds) { return refundMapper.deleteFishRefundByRefundIds(refundIds); }
}
