package com.ruoyi.fishing.service.impl;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
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
import com.ruoyi.fishing.service.IWxMiniappCodeService;

@Service
public class WxMiniappCodeServiceImpl implements IWxMiniappCodeService
{
    private static final Logger log = LoggerFactory.getLogger(WxMiniappCodeServiceImpl.class);

    @Autowired
    private WxProperties wxProperties;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper mapper = new ObjectMapper();
    private String cachedAccessToken;
    private long cachedExpireAt;

    @Override
    public byte[] createUnlimitedCode(String scene, String page, Integer width, String envVersion)
    {
        if (isBlank(scene)) throw new ServiceException("小程序码 scene 不能为空");
        if (scene.length() > 32) throw new ServiceException("小程序码 scene 不能超过 32 个字符");

        try {
            String token = getAccessToken();
            String url = wxProperties.getMiniapp().getWxaCodeUnlimitedUrl()
                    + "?access_token=" + URLEncoder.encode(token, StandardCharsets.UTF_8.name());

            Map<String, Object> req = new HashMap<>();
            req.put("scene", scene);
            req.put("page", isBlank(page) ? "pages/index/index" : page);
            req.put("width", width == null ? 430 : width);
            req.put("check_path", true);
            req.put("env_version", isBlank(envVersion) ? "release" : envVersion);
            req.put("is_hyaline", false);

            ResponseEntity<byte[]> res = restTemplate.postForEntity(url, req, byte[].class);
            byte[] body = res.getBody();
            if (body == null || body.length == 0) throw new ServiceException("微信小程序码生成失败");
            if (looksLikeJson(body)) {
                JsonNode node = mapper.readTree(body);
                throw new ServiceException("微信小程序码生成失败: " + node.path("errmsg").asText("unknown"));
            }
            return body;
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            log.error("生成微信小程序码失败", e);
            throw new ServiceException("生成微信小程序码失败");
        }
    }

    private synchronized String getAccessToken() throws Exception
    {
        long now = System.currentTimeMillis();
        if (!isBlank(cachedAccessToken) && now < cachedExpireAt) return cachedAccessToken;

        WxProperties.Miniapp mp = wxProperties.getMiniapp();
        if (isBlank(mp.getAppid()) || isBlank(mp.getSecret()) || "please-replace-me".equals(mp.getSecret())) {
            throw new ServiceException("小程序 AppID/AppSecret 未配置");
        }

        String url = mp.getAccessTokenUrl()
                + "?grant_type=client_credential"
                + "&appid=" + URLEncoder.encode(mp.getAppid(), StandardCharsets.UTF_8.name())
                + "&secret=" + URLEncoder.encode(mp.getSecret(), StandardCharsets.UTF_8.name());
        ResponseEntity<String> res = restTemplate.getForEntity(url, String.class);
        JsonNode node = mapper.readTree(res.getBody());
        String token = node.path("access_token").asText(null);
        if (isBlank(token)) throw new ServiceException("获取微信 access_token 失败: " + node.path("errmsg").asText("unknown"));

        int expiresIn = node.path("expires_in").asInt(7200);
        cachedAccessToken = token;
        cachedExpireAt = now + Math.max(60, expiresIn - 300) * 1000L;
        return token;
    }

    private boolean looksLikeJson(byte[] body)
    {
        for (byte b : body) {
            if (Character.isWhitespace((char) b)) continue;
            return b == '{' || b == '[';
        }
        return false;
    }

    private boolean isBlank(String value)
    {
        return value == null || value.trim().isEmpty();
    }
}
