package com.ruoyi.fishing.service;

public interface IWxMiniappCodeService
{
    byte[] createUnlimitedCode(String scene, String page, Integer width, String envVersion);
}
