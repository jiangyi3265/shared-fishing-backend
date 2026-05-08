package com.ruoyi.fishing.service;

public interface IWxAuthService
{
    /** 返回 openid；仅在 WX_MOCK_ENABLED=true 时允许 mock code */
    String resolveOpenid(String code);
}
