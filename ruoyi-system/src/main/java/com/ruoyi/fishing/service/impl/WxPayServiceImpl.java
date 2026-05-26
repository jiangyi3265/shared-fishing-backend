package com.ruoyi.fishing.service.impl;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.fishing.config.WxProperties;
import com.ruoyi.fishing.service.IWxPayService;

/**
 * 微信支付 V3 JSAPI 服务
 *
 * 通过反射调用 wechatpay-java SDK。开发环境可以关闭真实支付走 mock；
 * 生产环境只要 WX_PAY_ENABLED=true，SDK 或商户配置异常都会直接失败。
 *
 * 参考 SDK：https://github.com/wechatpay-apiv3/wechatpay-java
 */
@Service
public class WxPayServiceImpl implements IWxPayService
{
    private static final Logger log = LoggerFactory.getLogger(WxPayServiceImpl.class);

    @Autowired
    private WxProperties wxProperties;

    private Object jsapiService;
    private Object refundService;
    private Object notificationParser;
    private Object refundConfig;
    private boolean initialized = false;

    @Override
    public boolean isEnabled() { return wxProperties.getPay().isEnabled(); }

    @Override
    public boolean isMockEnabled() { return wxProperties.getPay().isMockEnabled(); }

    private synchronized void ensureInit()
    {
        if (initialized) return;
        if (!isEnabled()) return;
        validatePayConfig();
        try {
            WxProperties.Pay pay = wxProperties.getPay();
            Class<?> cfgBuilder = Class.forName("com.wechat.pay.java.core.RSAAutoCertificateConfig$Builder");
            Object builder = cfgBuilder.getDeclaredConstructor().newInstance();
            cfgBuilder.getMethod("merchantId", String.class).invoke(builder, pay.getMchId());
            cfgBuilder.getMethod("privateKeyFromPath", String.class).invoke(builder, pay.getPrivateKeyPath());
            cfgBuilder.getMethod("merchantSerialNumber", String.class).invoke(builder, pay.getCertSerial());
            cfgBuilder.getMethod("apiV3Key", String.class).invoke(builder, pay.getApiV3Key());
            Object config = cfgBuilder.getMethod("build").invoke(builder);

            Class<?> svcBuilder = Class.forName("com.wechat.pay.java.service.payments.jsapi.JsapiServiceExtension$Builder");
            Object sb = svcBuilder.getDeclaredConstructor().newInstance();
            svcBuilder.getMethod("config", Class.forName("com.wechat.pay.java.core.Config")).invoke(sb, config);
            jsapiService = svcBuilder.getMethod("build").invoke(sb);

            Class<?> parser = Class.forName("com.wechat.pay.java.core.notification.NotificationParser");
            notificationParser = parser.getConstructor(Class.forName("com.wechat.pay.java.core.notification.NotificationConfig"))
                    .newInstance(config);
            refundConfig = config;

            Class<?> refundBuilder = Class.forName("com.wechat.pay.java.service.refund.RefundService$Builder");
            Object rb = refundBuilder.getDeclaredConstructor().newInstance();
            refundBuilder.getMethod("config", Class.forName("com.wechat.pay.java.core.Config")).invoke(rb, config);
            refundService = refundBuilder.getMethod("build").invoke(rb);

            initialized = true;
            log.info("WxPay SDK 初始化成功");
        } catch (ServiceException e) {
            throw e;
        } catch (ClassNotFoundException e) {
            log.error("wechatpay-java SDK 未在 classpath 中", e);
            throw new ServiceException("微信支付 SDK 未安装");
        } catch (Throwable t) {
            log.error("微信支付初始化失败", t);
            throw new ServiceException("微信支付初始化失败：" + rootMessage(t));
        }
    }

    private void validatePayConfig()
    {
        WxProperties.Pay pay = wxProperties.getPay();
        if (isBlank(pay.getMchId())) throw new ServiceException("微信支付初始化失败：WX_PAY_MCH_ID 未配置");
        if (isBlank(pay.getApiV3Key())) throw new ServiceException("微信支付初始化失败：WX_PAY_APIV3 未配置");
        if (pay.getApiV3Key().length() != 32) throw new ServiceException("微信支付初始化失败：WX_PAY_APIV3 必须为32位");
        if (isBlank(pay.getNotifyUrl()) || !pay.getNotifyUrl().startsWith("https://")) {
            throw new ServiceException("微信支付初始化失败：WX_PAY_NOTIFY 必须是 HTTPS 地址");
        }
        if (isBlank(pay.getPrivateKeyPath())) throw new ServiceException("微信支付初始化失败：WX_PAY_PRIVATE_KEY 未配置");
        File privateKey = new File(pay.getPrivateKeyPath());
        if (!privateKey.isFile() || !privateKey.canRead()) {
            throw new ServiceException("微信支付初始化失败：商户私钥文件不存在或不可读");
        }
        if (isBlank(pay.getCertSerial())) throw new ServiceException("微信支付初始化失败：WX_PAY_CERT_SERIAL 未配置");
    }

    private boolean isBlank(String value)
    {
        return value == null || value.trim().isEmpty();
    }

    private String rootMessage(Throwable t)
    {
        Throwable cur = t;
        while (cur.getCause() != null) cur = cur.getCause();
        String msg = cur.getMessage();
        if (isBlank(msg)) msg = cur.getClass().getSimpleName();
        return msg;
    }

    @Override
    public Map<String, Object> createPrepay(String orderNo, int amountCents, String openid, String description)
    {
        Map<String, Object> resp = new HashMap<>();
        ensureInit();
        if (!initialized)
        {
            if (isEnabled() || !isMockEnabled()) throw new ServiceException("微信支付未初始化");
            resp.put("mock", true);
            resp.put("tradeNo", "MOCK" + System.currentTimeMillis());
            resp.put("prepayId", "mock_prepay_" + orderNo);
            return resp;
        }
        try {
            WxProperties.Miniapp mp = wxProperties.getMiniapp();
            WxProperties.Pay pay = wxProperties.getPay();
            if (isBlank(mp.getAppid())) throw new ServiceException("微信支付下单失败：WX_APPID 未配置");
            if (isBlank(openid)) throw new ServiceException("微信支付下单失败：当前用户缺少 openid，请重新登录");
            if (isBlank(orderNo)) throw new ServiceException("微信支付下单失败：订单号为空");
            if (amountCents <= 0) throw new ServiceException("微信支付下单失败：支付金额必须大于0");

            Class<?> reqCls = Class.forName("com.wechat.pay.java.service.payments.jsapi.model.PrepayRequest");
            Class<?> amtCls = Class.forName("com.wechat.pay.java.service.payments.jsapi.model.Amount");
            Class<?> payerCls = Class.forName("com.wechat.pay.java.service.payments.jsapi.model.Payer");
            Object req = reqCls.getDeclaredConstructor().newInstance();
            reqCls.getMethod("setAppid", String.class).invoke(req, mp.getAppid());
            reqCls.getMethod("setMchid", String.class).invoke(req, pay.getMchId());
            reqCls.getMethod("setDescription", String.class).invoke(req, description);
            reqCls.getMethod("setOutTradeNo", String.class).invoke(req, orderNo);
            reqCls.getMethod("setNotifyUrl", String.class).invoke(req, pay.getNotifyUrl());

            Object amount = amtCls.getDeclaredConstructor().newInstance();
            amtCls.getMethod("setTotal", Integer.class).invoke(amount, amountCents);
            amtCls.getMethod("setCurrency", String.class).invoke(amount, "CNY");
            reqCls.getMethod("setAmount", amtCls).invoke(req, amount);

            Object payer = payerCls.getDeclaredConstructor().newInstance();
            payerCls.getMethod("setOpenid", String.class).invoke(payer, openid);
            reqCls.getMethod("setPayer", payerCls).invoke(req, payer);

            Object result = jsapiService.getClass()
                    .getMethod("prepayWithRequestPayment", reqCls)
                    .invoke(jsapiService, req);

            resp.put("mock", false);
            resp.put("appId", invoke(result, "getAppId"));
            resp.put("timeStamp", invoke(result, "getTimeStamp"));
            resp.put("nonceStr", invoke(result, "getNonceStr"));
            resp.put("package", invoke(result, "getPackageVal"));
            resp.put("signType", invoke(result, "getSignType"));
            resp.put("paySign", invoke(result, "getPaySign"));
            return resp;
        } catch (ServiceException e) {
            throw e;
        } catch (Throwable t) {
            log.error("微信支付下单失败", t);
            throw new ServiceException("微信支付下单失败：" + rootMessage(t));
        }
    }

    @Override
    public String handleNotify(String body, Map<String, String> headers)
    {
        ensureInit();
        if (!initialized) return null;
        try {
            Class<?> reqCls = Class.forName("com.wechat.pay.java.core.notification.RequestParam$Builder");
            Object b = reqCls.getDeclaredConstructor().newInstance();
            reqCls.getMethod("serialNumber", String.class).invoke(b, header(headers, "Wechatpay-Serial"));
            reqCls.getMethod("nonce", String.class).invoke(b, header(headers, "Wechatpay-Nonce"));
            reqCls.getMethod("signature", String.class).invoke(b, header(headers, "Wechatpay-Signature"));
            reqCls.getMethod("timestamp", String.class).invoke(b, header(headers, "Wechatpay-Timestamp"));
            reqCls.getMethod("body", String.class).invoke(b, body);
            Object param = reqCls.getMethod("build").invoke(b);

            Class<?> txCls = Class.forName("com.wechat.pay.java.service.payments.model.Transaction");
            Object tx = notificationParser.getClass()
                    .getMethod("parse", Class.forName("com.wechat.pay.java.core.notification.RequestParam"), Class.class)
                    .invoke(notificationParser, param, txCls);
            return (String) txCls.getMethod("getOutTradeNo").invoke(tx);
        } catch (Throwable t) {
            log.error("解析微信支付回调失败", t);
            return null;
        }
    }

    @Override
    public String refund(String orderNo, String refundNo, int amountCents, int totalCents, String reason)
    {
        ensureInit();
        if (!initialized)
        {
            if (isEnabled() || !isMockEnabled()) throw new ServiceException("微信退款未初始化");
            return "MOCK_REFUND_" + System.currentTimeMillis();
        }
        try {
            WxProperties.Pay pay = wxProperties.getPay();
            Class<?> reqCls = Class.forName("com.wechat.pay.java.service.refund.model.CreateRequest");
            Class<?> amtCls = Class.forName("com.wechat.pay.java.service.refund.model.AmountReq");
            Object req = reqCls.getDeclaredConstructor().newInstance();
            reqCls.getMethod("setOutTradeNo", String.class).invoke(req, orderNo);
            reqCls.getMethod("setOutRefundNo", String.class).invoke(req, refundNo);
            if (reason != null && !reason.isEmpty())
            {
                try { reqCls.getMethod("setReason", String.class).invoke(req, reason); } catch (NoSuchMethodException ignore) {}
            }
            reqCls.getMethod("setNotifyUrl", String.class).invoke(req, pay.getNotifyUrl().replaceAll("/notify$", "/refund/notify"));

            Object amount = amtCls.getDeclaredConstructor().newInstance();
            amtCls.getMethod("setRefund", Long.class).invoke(amount, (long) amountCents);
            amtCls.getMethod("setTotal",  Long.class).invoke(amount, (long) totalCents);
            amtCls.getMethod("setCurrency", String.class).invoke(amount, "CNY");
            reqCls.getMethod("setAmount", amtCls).invoke(req, amount);

            Object refund = refundService.getClass().getMethod("create", reqCls).invoke(refundService, req);
            Object id = refund.getClass().getMethod("getRefundId").invoke(refund);
            return id == null ? "" : id.toString();
        } catch (ServiceException e) {
            throw e;
        } catch (Throwable t) {
            log.error("微信退款下单失败", t);
            throw new ServiceException("微信退款下单失败: " + t.getMessage());
        }
    }

    @Override
    public RefundCallback handleRefundNotify(String body, Map<String, String> headers)
    {
        ensureInit();
        if (!initialized) return null;
        try {
            Class<?> reqCls = Class.forName("com.wechat.pay.java.core.notification.RequestParam$Builder");
            Object b = reqCls.getDeclaredConstructor().newInstance();
            reqCls.getMethod("serialNumber", String.class).invoke(b, header(headers, "Wechatpay-Serial"));
            reqCls.getMethod("nonce",        String.class).invoke(b, header(headers, "Wechatpay-Nonce"));
            reqCls.getMethod("signature",    String.class).invoke(b, header(headers, "Wechatpay-Signature"));
            reqCls.getMethod("timestamp",    String.class).invoke(b, header(headers, "Wechatpay-Timestamp"));
            reqCls.getMethod("body",         String.class).invoke(b, body);
            Object param = reqCls.getMethod("build").invoke(b);

            Class<?> refundNotifyCls = Class.forName("com.wechat.pay.java.service.refund.model.RefundNotification");
            Object n = notificationParser.getClass()
                    .getMethod("parse", Class.forName("com.wechat.pay.java.core.notification.RequestParam"), Class.class)
                    .invoke(notificationParser, param, refundNotifyCls);
            RefundCallback cb = new RefundCallback();
            cb.refundNo  = (String) refundNotifyCls.getMethod("getOutRefundNo").invoke(n);
            cb.wxRefundNo = (String) refundNotifyCls.getMethod("getRefundId").invoke(n);
            Object status = refundNotifyCls.getMethod("getRefundStatus").invoke(n);
            cb.success = status != null && "SUCCESS".equals(status.toString());
            return cb;
        } catch (Throwable t) {
            log.error("解析微信退款回调失败", t);
            return null;
        }
    }

    private Object invoke(Object target, String method) throws Exception
    {
        return target.getClass().getMethod(method).invoke(target);
    }

    private String header(Map<String, String> headers, String name)
    {
        String exact = headers.get(name);
        if (exact != null) return exact;
        String lower = headers.get(name.toLowerCase());
        if (lower != null) return lower;
        for (Map.Entry<String, String> entry : headers.entrySet())
        {
            if (name.equalsIgnoreCase(entry.getKey())) return entry.getValue();
        }
        return "";
    }
}
