package com.ruoyi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Spring 启动前的生产配置保护。
 */
public final class ProductionStartupCheck
{
    private static final String DEV_TOKEN_SECRET = "local-dev-token-secret-change-before-production";
    private static final String WX_SECRET_PLACEHOLDER = "please-replace-me";
    private static final String DEV_DB_PASSWORD = "123456";

    private ProductionStartupCheck()
    {
    }

    public static void validate(String[] args)
    {
        Map<String, String> argMap = parseArgs(args);
        String appEnv = firstValue(argMap, "app.env", "APP_ENV");
        if (isBlank(appEnv))
        {
            appEnv = System.getenv("APP_ENV");
        }
        if (!"production".equalsIgnoreCase(appEnv))
        {
            return;
        }

        List<String> errors = new ArrayList<>();
        String dbPassword = configValue(argMap, "spring.datasource.druid.master.password", "DB_PASSWORD");
        String tokenSecret = configValue(argMap, "token.secret", "TOKEN_SECRET");
        String wxAppid = configValue(argMap, "wx.miniapp.appid", "WX_APPID");
        String wxSecret = configValue(argMap, "wx.miniapp.secret", "WX_SECRET");
        String wxMockEnabled = configValue(argMap, "wx.miniapp.mock-enabled", "WX_MOCK_ENABLED");
        if (isBlank(wxMockEnabled))
        {
            wxMockEnabled = "true";
        }
        String wxPayEnabled = configValue(argMap, "wx.pay.enabled", "WX_PAY_ENABLED");
        String wxPayMockEnabled = configValue(argMap, "wx.pay.mock-enabled", "WX_PAY_MOCK_ENABLED");
        if (isBlank(wxPayMockEnabled))
        {
            wxPayMockEnabled = "true";
        }
        String wxPayMchId = configValue(argMap, "wx.pay.mch-id", "WX_PAY_MCH_ID");
        String wxPayApiV3 = configValue(argMap, "wx.pay.api-v3-key", "WX_PAY_APIV3");
        String wxPayNotify = configValue(argMap, "wx.pay.notify-url", "WX_PAY_NOTIFY");
        String wxPayPrivateKey = configValue(argMap, "wx.pay.private-key-path", "WX_PAY_PRIVATE_KEY");
        String wxPayCertSerial = configValue(argMap, "wx.pay.cert-serial", "WX_PAY_CERT_SERIAL");
        String wxPayPublicKey = configValue(argMap, "wx.pay.public-key-path", "WX_PAY_PUBLIC_KEY");
        String wxPayPublicKeyId = configValue(argMap, "wx.pay.public-key-id", "WX_PAY_PUBLIC_KEY_ID");
        String corsOrigins = configValue(argMap, "cors.allowed-origin-patterns", "CORS_ALLOWED_ORIGINS");

        if (isBlank(dbPassword) || DEV_DB_PASSWORD.equals(dbPassword))
        {
            errors.add("DB_PASSWORD 未配置或仍为开发默认值");
        }
        if (isBlank(tokenSecret) || DEV_TOKEN_SECRET.equals(tokenSecret) || tokenSecret.length() < 32)
        {
            errors.add("TOKEN_SECRET 必须替换为至少 32 位随机值");
        }
        if (isBlank(wxSecret) || WX_SECRET_PLACEHOLDER.equals(wxSecret))
        {
            errors.add("WX_SECRET 未配置");
        }
        if (isBlank(wxAppid) || !wxAppid.startsWith("wx"))
        {
            errors.add("WX_APPID 未配置或格式不正确");
        }
        if ("true".equalsIgnoreCase(wxMockEnabled))
        {
            errors.add("WX_MOCK_ENABLED 生产环境必须为 false");
        }
        if (!"true".equalsIgnoreCase(wxPayEnabled))
        {
            errors.add("WX_PAY_ENABLED 生产环境必须为 true");
        }
        if ("true".equalsIgnoreCase(wxPayMockEnabled))
        {
            errors.add("WX_PAY_MOCK_ENABLED 生产环境必须为 false");
        }
        if (isPlaceholder(wxPayMchId))
        {
            errors.add("WX_PAY_MCH_ID 未配置");
        }
        if (isPlaceholder(wxPayApiV3))
        {
            errors.add("WX_PAY_APIV3 未配置");
        }
        if (isBlank(wxPayNotify) || !wxPayNotify.startsWith("https://"))
        {
            errors.add("WX_PAY_NOTIFY 必须配置为 HTTPS 回调地址");
        }
        if (isPlaceholder(wxPayPrivateKey))
        {
            errors.add("WX_PAY_PRIVATE_KEY 未配置");
        }
        if (isPlaceholder(wxPayCertSerial))
        {
            errors.add("WX_PAY_CERT_SERIAL 未配置");
        }
        if (isPlaceholder(wxPayPublicKey))
        {
            errors.add("WX_PAY_PUBLIC_KEY 未配置");
        }
        if (isPlaceholder(wxPayPublicKeyId))
        {
            errors.add("WX_PAY_PUBLIC_KEY_ID 未配置");
        }
        if (isBlank(corsOrigins) || containsLocalhost(corsOrigins))
        {
            errors.add("CORS_ALLOWED_ORIGINS 必须替换为正式域名");
        }
        if ("true".equalsIgnoreCase(configValue(argMap, "swagger.enabled", "SWAGGER_ENABLED")))
        {
            errors.add("SWAGGER_ENABLED 生产环境必须为 false");
        }
        if ("true".equalsIgnoreCase(configValue(argMap, "demo.enabled", "DEMO_ENABLED")))
        {
            errors.add("DEMO_ENABLED 生产环境必须为 false");
        }
        if (!errors.isEmpty())
        {
            throw new IllegalStateException("生产配置未完成: " + String.join("; ", errors));
        }
    }

    private static Map<String, String> parseArgs(String[] args)
    {
        Map<String, String> map = new HashMap<>();
        if (args == null)
        {
            return map;
        }
        for (String arg : args)
        {
            if (arg == null || !arg.startsWith("--"))
            {
                continue;
            }
            int split = arg.indexOf('=');
            if (split > 2)
            {
                map.put(arg.substring(2, split), arg.substring(split + 1));
            }
        }
        return map;
    }

    private static String configValue(Map<String, String> argMap, String propertyName, String envName)
    {
        String value = firstValue(argMap, propertyName, envName);
        return isBlank(value) ? System.getenv(envName) : value;
    }

    private static String firstValue(Map<String, String> argMap, String firstKey, String secondKey)
    {
        String value = argMap.get(firstKey);
        return isBlank(value) ? argMap.get(secondKey) : value;
    }

    private static boolean isBlank(String value)
    {
        return value == null || value.trim().isEmpty();
    }

    private static boolean isPlaceholder(String value)
    {
        return isBlank(value) || value.startsWith("replace-with");
    }

    private static boolean containsLocalhost(String value)
    {
        return value.contains("localhost") || value.contains("127.0.0.1");
    }
}
