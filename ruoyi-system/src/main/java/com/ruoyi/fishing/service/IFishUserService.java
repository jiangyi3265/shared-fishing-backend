package com.ruoyi.fishing.service;

import com.ruoyi.fishing.domain.FishUser;

public interface IFishUserService
{
    public FishUser selectFishUserByUserId(Long userId);
    public FishUser selectFishUserByOpenid(String openid);
    public java.util.List<FishUser> selectFishUserList(FishUser user);
    public FishUser loginOrRegister(String openid, String nickname, String avatar);
    public int updateFishUser(FishUser user);
}
