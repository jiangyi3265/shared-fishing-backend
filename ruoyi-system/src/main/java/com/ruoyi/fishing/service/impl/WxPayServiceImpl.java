package com.ruoyi.fishing.service.impl;

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
    private Object notificationParser;
    private boolean initialized = false;

    @Override
    public boolean isEnabled() { return wxProperties.getPay().isEnabled(); }

    @Override
    public boolean isMockEnabled() { return wxProperties.getPay().isMockEnabled(); }

    private synchronized void ensureInit()
    {
        if (initialized) return;
        if (!isEnabled()) return;
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

            initialized = true;
            log.info("WxPay SDK 初始化成功");
        } catch (ClassNotFoundException e) {
            log.error("wechatpay-java SDK 未在 classpath 中", e);
            throw new ServiceException("微信支付 SDK 未安装");
        } catch (Throwable t) {
            log.error("微信支付初始化失败", t);
            throw new ServiceException("微信支付初始化失败");
        }
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
        } catch (Throwable t) {
            log.error("微信支付下单失败", t);
            throw new ServiceException("微信支付下单失败");
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
