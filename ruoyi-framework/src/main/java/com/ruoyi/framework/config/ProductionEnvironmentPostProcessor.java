package com.ruoyi.framework.config;

import java.util.ArrayList;
import java.util.List;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * 在任何数据源初始化前校验生产环境占位配置。
 */
public class ProductionEnvironmentPostProcessor implements EnvironmentPostProcessor, Ordered
{
    private static final String DEV_TOKEN_SECRET = "local-dev-token-secret-change-before-production";
    private static final String WX_SECRET_PLACEHOLDER = "please-replace-me";
    private static final String DEV_DB_PASSWORD = "123456";

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application)
    {
        String appEnv = environment.getProperty("app.env", "development");
        if (!"production".equalsIgnoreCase(appEnv))
        {
            return;
        }

        List<String> errors = new ArrayList<>();
        String dbPassword = environment.getProperty("spring.datasource.druid.master.password", "");
        String tokenSecret = environment.getProperty("token.secret", "");
        String wxSecret = environment.getProperty("wx.miniapp.secret", "");
        String corsOrigins = environment.getProperty("cors.allowed-origin-patterns", "");
        boolean wxMockEnabled = environment.getProperty("wx.miniapp.mock-enabled", Boolean.class, true);
        boolean wxPayEnabled = environment.getProperty("wx.pay.enabled", Boolean.class, false);
        boolean wxPayMockEnabled = environment.getProperty("wx.pay.mock-enabled", Boolean.class, true);
        String wxPayMchId = environment.getProperty("wx.pay.mch-id", "");
        String wxPayApiV3 = environment.getProperty("wx.pay.api-v3-key", "");
        String wxPayNotify = environment.getProperty("wx.pay.notify-url", "");
        String wxPayPrivateKey = environment.getProperty("wx.pay.private-key-path", "");
        String wxPayCertSerial = environment.getProperty("wx.pay.cert-serial", "");
        String wxPayPublicKey = environment.getProperty("wx.pay.public-key-path", "");
        String wxPayPublicKeyId = environment.getProperty("wx.pay.public-key-id", "");

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
        if (wxMockEnabled)
        {
            errors.add("WX_MOCK_ENABLED 生产环境必须为 false");
        }
        if (!wxPayEnabled)
        {
            errors.add("WX_PAY_ENABLED 生产环境必须为 true");
        }
        if (wxPayMockEnabled)
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
        if (environment.getProperty("swagger.enabled", Boolean.class, false))
        {
            errors.add("SWAGGER_ENABLED 生产环境必须为 false");
        }
        if (environment.getProperty("demo.enabled", Boolean.class, false))
        {
            errors.add("DEMO_ENABLED 生产环境必须为 false");
        }
        if (!errors.isEmpty())
        {
            throw new IllegalStateException("生产配置未完成: " + String.join("; ", errors));
        }
    }

    @Override
    public int getOrder()
    {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    private boolean isBlank(String value)
    {
        return value == null || value.trim().isEmpty();
    }

    private boolean isPlaceholder(String value)
    {
        return isBlank(value) || value.startsWith("replace-with");
    }

    private boolean containsLocalhost(String value)
    {
        return value.contains("localhost") || value.contains("127.0.0.1");
    }
}
