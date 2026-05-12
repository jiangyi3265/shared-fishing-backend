package com.ruoyi.fishing.service;

import java.util.Map;

public interface IWxPayService
{
    /** 是否启用真实微信支付 */
    boolean isEnabled();

    boolean isMockEnabled();

    /** 发起下单，返回前端 wx.requestPayment 需要的参数（或 mock 流水号） */
    Map<String, Object> createPrepay(String orderNo, int amountCents, String openid, String description);

    /** 处理微信支付回调，返回订单号 */
    String handleNotify(String body, Map<String, String> headers);

    /**
     * 发起微信退款。amountCents 是本次退款金额；totalCents 是订单原总额（必填）。
     * 返回微信侧 refund_id（成功；异步退款返回中可能为空字符串）。
     * 调用方应将状态置为退款中，待 wx 回调 handleRefundNotify 推进。
     */
    String refund(String orderNo, String refundNo, int amountCents, int totalCents, String reason);

    /**
     * 处理微信退款回调。返回 RefundCallback（refundNo + success），上层据此推进退款单状态。
     */
    RefundCallback handleRefundNotify(String body, Map<String, String> headers);

    class RefundCallback
    {
        public String refundNo;
        public boolean success;
        public String wxRefundNo;
    }
}
