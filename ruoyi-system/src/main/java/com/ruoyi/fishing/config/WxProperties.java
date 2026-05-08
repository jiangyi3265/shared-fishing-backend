package com.ruoyi.fishing.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "wx")
public class WxProperties
{
    private Miniapp miniapp = new Miniapp();
    private Pay pay = new Pay();

    public Miniapp getMiniapp() { return miniapp; }
    public void setMiniapp(Miniapp miniapp) { this.miniapp = miniapp; }
    public Pay getPay() { return pay; }
    public void setPay(Pay pay) { this.pay = pay; }

    public static class Miniapp
    {
        private String appid;
        private String secret;
        private String code2sessionUrl = "https://api.weixin.qq.com/sns/jscode2session";
        private boolean mockEnabled;
        private int tokenExpireDays = 30;

        public String getAppid() { return appid; }
        public void setAppid(String appid) { this.appid = appid; }
        public String getSecret() { return secret; }
        public void setSecret(String secret) { this.secret = secret; }
        public String getCode2sessionUrl() { return code2sessionUrl; }
        public void setCode2sessionUrl(String code2sessionUrl) { this.code2sessionUrl = code2sessionUrl; }
        public boolean isMockEnabled() { return mockEnabled; }
        public void setMockEnabled(boolean mockEnabled) { this.mockEnabled = mockEnabled; }
        public int getTokenExpireDays() { return tokenExpireDays; }
        public void setTokenExpireDays(int tokenExpireDays) { this.tokenExpireDays = tokenExpireDays; }
    }

    public static class Pay
    {
        private boolean enabled;
        private boolean mockEnabled;
        private String mchId;
        private String apiV3Key;
        private String notifyUrl;
        private String privateKeyPath;
        private String certSerial;

        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }
        public boolean isMockEnabled() { return mockEnabled; }
        public void setMockEnabled(boolean mockEnabled) { this.mockEnabled = mockEnabled; }
        public String getMchId() { return mchId; }
        public void setMchId(String mchId) { this.mchId = mchId; }
        public String getApiV3Key() { return apiV3Key; }
        public void setApiV3Key(String apiV3Key) { this.apiV3Key = apiV3Key; }
        public String getNotifyUrl() { return notifyUrl; }
        public void setNotifyUrl(String notifyUrl) { this.notifyUrl = notifyUrl; }
        public String getPrivateKeyPath() { return privateKeyPath; }
        public void setPrivateKeyPath(String privateKeyPath) { this.privateKeyPath = privateKeyPath; }
        public String getCertSerial() { return certSerial; }
        public void setCertSerial(String certSerial) { this.certSerial = certSerial; }
    }
}
