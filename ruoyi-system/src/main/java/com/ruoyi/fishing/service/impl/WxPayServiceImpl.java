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
 * 通过反射调用 wechatpay-java SDK（可选依赖），这样即使构建环境无法联网拉取 SDK，
 * 项目也能编译运行，只是走 mock 通道。上线前：
 *   1. 在 ruoyi-system/pom.xml 保留 wechatpay-java 依赖（已添加）
 *   2. 配置 WX_PAY_* 环境变量
 *   3. WX_PAY_ENABLED=true
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
            log.warn("wechatpay-java SDK 未在 classpath 中，使用 mock 通道。引入依赖重新构建后生效。");
        } catch (Throwable t) {
            log.error("微信支付初始化失败，降级为 mock", t);
        }
    }

    @Override
    public Map<String, Object> createPrepay(String orderNo, int amountCents, String openid, String description)
    {
        Map<String, Object> resp = new HashMap<>();
        ensureInit();
        if (!initialized)
        {
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
            reqCls.getMethod("serialNumber", String.class).invoke(b, headers.getOrDefault("Wechatpay-Serial", ""));
            reqCls.getMethod("nonce", String.class).invoke(b, headers.getOrDefault("Wechatpay-Nonce", ""));
            reqCls.getMethod("signature", String.class).invoke(b, headers.getOrDefault("Wechatpay-Signature", ""));
            reqCls.getMethod("timestamp", String.class).invoke(b, headers.getOrDefault("Wechatpay-Timestamp", ""));
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
}
