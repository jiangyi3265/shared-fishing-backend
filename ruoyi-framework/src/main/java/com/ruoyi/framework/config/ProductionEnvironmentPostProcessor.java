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
        String dbPassword = configValue(environment, "spring.datasource.druid.master.password", "DB_PASSWORD", "");
        String tokenSecret = configValue(environment, "token.secret", "TOKEN_SECRET", "");
        String wxSecret = configValue(environment, "wx.miniapp.secret", "WX_SECRET", "");
        String corsOrigins = configValue(environment, "cors.allowed-origin-patterns", "CORS_ALLOWED_ORIGINS", "");
        boolean wxMockEnabled = configBoolean(environment, "wx.miniapp.mock-enabled", "WX_MOCK_ENABLED", true);
        boolean wxPayEnabled = configBoolean(environment, "wx.pay.enabled", "WX_PAY_ENABLED", false);
        boolean wxPayMockEnabled = configBoolean(environment, "wx.pay.mock-enabled", "WX_PAY_MOCK_ENABLED", true);
        String wxPayMchId = configValue(environment, "wx.pay.mch-id", "WX_PAY_MCH_ID", "");
        String wxPayApiV3 = configValue(environment, "wx.pay.api-v3-key", "WX_PAY_APIV3", "");
        String wxPayNotify = configValue(environment, "wx.pay.notify-url", "WX_PAY_NOTIFY", "");
        String wxPayPrivateKey = configValue(environment, "wx.pay.private-key-path", "WX_PAY_PRIVATE_KEY", "");
        String wxPayCertSerial = configValue(environment, "wx.pay.cert-serial", "WX_PAY_CERT_SERIAL", "");
        String wxPayPublicKey = configValue(environment, "wx.pay.public-key-path", "WX_PAY_PUBLIC_KEY", "");
        String wxPayPublicKeyId = configValue(environment, "wx.pay.public-key-id", "WX_PAY_PUBLIC_KEY_ID", "");

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
        if (configBoolean(environment, "swagger.enabled", "SWAGGER_ENABLED", false))
        {
            errors.add("SWAGGER_ENABLED 生产环境必须为 false");
        }
        if (configBoolean(environment, "demo.enabled", "DEMO_ENABLED", false))
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

    private String configValue(ConfigurableEnvironment environment, String propertyName, String envName, String defaultValue)
    {
        String value = environment.getProperty(propertyName);
        if (isBlank(value))
        {
            value = environment.getProperty(envName);
        }
        if (isBlank(value))
        {
            value = System.getenv(envName);
        }
        return isBlank(value) ? defaultValue : value;
    }

    private boolean configBoolean(ConfigurableEnvironment environment, String propertyName, String envName, boolean defaultValue)
    {
        String value = configValue(environment, propertyName, envName, "");
        return isBlank(value) ? defaultValue : Boolean.parseBoolean(value);
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
