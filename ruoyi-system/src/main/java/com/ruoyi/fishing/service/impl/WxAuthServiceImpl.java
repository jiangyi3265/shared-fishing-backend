package com.ruoyi.fishing.service.impl;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.fishing.config.WxProperties;
import com.ruoyi.fishing.service.IWxAuthService;

@Service
public class WxAuthServiceImpl implements IWxAuthService
{
    private static final Logger log = LoggerFactory.getLogger(WxAuthServiceImpl.class);

    @Autowired
    private WxProperties wxProperties;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public String resolveOpenid(String code)
    {
        if (code == null || code.isEmpty()) throw new ServiceException("缺少登录 code");

        WxProperties.Miniapp mp = wxProperties.getMiniapp();
        if (mp.isMockEnabled() && code.startsWith("mock_")) return code;
        boolean missingCredentials = isBlank(mp.getAppid()) || isBlank(mp.getSecret())
                || "please-replace-me".equals(mp.getSecret());
        if (missingCredentials) {
            if (mp.isMockEnabled()) {
                log.warn("微信小程序未配置 appid/secret，开发模式使用固定 mock openid");
                return "mock_openid_dev";
            }
            throw new ServiceException("微信小程序 AppSecret 未配置");
        }

        try {
            String url = mp.getCode2sessionUrl()
                    + "?appid=" + URLEncoder.encode(mp.getAppid(), StandardCharsets.UTF_8.name())
                    + "&secret=" + URLEncoder.encode(mp.getSecret(), StandardCharsets.UTF_8.name())
                    + "&js_code=" + URLEncoder.encode(code, StandardCharsets.UTF_8.name())
                    + "&grant_type=authorization_code";
            ResponseEntity<String> res = restTemplate.getForEntity(url, String.class);
            JsonNode node = mapper.readTree(res.getBody());
            String openid = node.path("openid").asText(null);
            if (openid == null || openid.isEmpty()) {
                throw new ServiceException("微信登录失败: " + node.path("errmsg").asText("unknown"));
            }
            return openid;
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            log.error("调用 jscode2session 失败", e);
            throw new ServiceException("微信登录失败，请稍后重试");
        }
    }

    private boolean isBlank(String value)
    {
        return value == null || value.trim().isEmpty();
    }
}
