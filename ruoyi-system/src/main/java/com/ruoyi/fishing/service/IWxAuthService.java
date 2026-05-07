package com.ruoyi.fishing.service;

public interface IWxAuthService
{
    /** 返回 openid；code 为 "mock_*" 时跳过真实调用，便于本地联调 */
    String resolveOpenid(String code);
}
