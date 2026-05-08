package com.ruoyi.fishing.service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.ruoyi.common.exception.ServiceException;

@Service
public class AppTokenService
{
    private static final String PREFIX = "app.";

    @Value("${token.secret}")
    private String secret;

    @Value("${wx.miniapp.token-expire-days:30}")
    private int expireDays;

    public String createToken(Long userId)
    {
        if (userId == null) throw new ServiceException("缺少用户ID");
        long expiresAt = System.currentTimeMillis() + Math.max(1, expireDays) * 24L * 60L * 60L * 1000L;
        String payload = userId + "." + expiresAt;
        return PREFIX + payload + "." + sign(payload);
    }

    public Long resolveUserId(String authorization)
    {
        String token = normalize(authorization);
        if (token == null) return null;
        String[] parts = token.split("\\.");
        if (parts.length != 4 || !"app".equals(parts[0])) return null;
        String payload = parts[1] + "." + parts[2];
        if (!sign(payload).equals(parts[3])) return null;
        long expiresAt;
        try {
            expiresAt = Long.parseLong(parts[2]);
        } catch (NumberFormatException e) {
            return null;
        }
        if (expiresAt < System.currentTimeMillis()) return null;
        try {
            return Long.parseLong(parts[1]);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String normalize(String authorization)
    {
        if (authorization == null || authorization.trim().isEmpty()) return null;
        String value = authorization.trim();
        if (value.startsWith("Bearer ")) value = value.substring(7).trim();
        return value.startsWith(PREFIX) ? value : null;
    }

    private String sign(String payload)
    {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            return Base64.getUrlEncoder().withoutPadding()
                    .encodeToString(mac.doFinal(payload.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new ServiceException("生成登录凭证失败");
        }
    }
}
