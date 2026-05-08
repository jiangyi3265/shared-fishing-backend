package com.ruoyi.fishing.service;

import java.util.Map;

public interface IWxPayService
{
    /** 是否启用真实微信支付 */
    boolean isEnabled();

    boolean isMockEnabled();

    /** 发起下单，返回前端 wx.requestPayment 需要的参数（或 mock 流水号） */
    Map<String, Object> createPrepay(String orderNo, int amountCents, String openid, String description);

    /** 处理微信回调，返回订单号 */
    String handleNotify(String body, Map<String, String> headers);
}
