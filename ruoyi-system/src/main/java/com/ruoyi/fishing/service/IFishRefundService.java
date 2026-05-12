package com.ruoyi.fishing.service;

import java.util.List;
import com.ruoyi.fishing.domain.FishRefund;

public interface IFishRefundService
{
    FishRefund selectFishRefundByRefundId(Long refundId);
    List<FishRefund> selectFishRefundList(FishRefund refund);
    List<FishRefund> selectByUserId(Long userId);

    /** 用户申请钓场订单退款。一个订单同时只允许一条 active(0/1) 退款 */
    FishRefund applyRefund(Long userId, Long orderId, Integer applyAmountCents, String reason);

    /** 用户申请商城订单退款 */
    FishRefund applyMallRefund(Long userId, Long mallOrderId, Integer applyAmountCents, String reason);

    /**
     * 管理员通过：调用微信退款（或 mock）。
     * mock 模式下直接置为已完成；真实模式下置为退款中，等待 wx 回调。
     */
    FishRefund approve(Long refundId, Integer refundAmountCents, String remark, String operator);

    /** 管理员驳回 */
    FishRefund reject(Long refundId, String remark, String operator);

    /** 微信退款回调（成功或失败） */
    FishRefund handleRefundCallback(String refundNo, boolean success, String wxRefundNo);

    int deleteFishRefundByRefundIds(Long[] refundIds);
}
