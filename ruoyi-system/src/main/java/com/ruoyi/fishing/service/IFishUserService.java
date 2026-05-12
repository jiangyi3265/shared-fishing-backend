package com.ruoyi.fishing.service;

import com.ruoyi.fishing.domain.FishUser;

public interface IFishUserService
{
    public FishUser selectFishUserByUserId(Long userId);
    public FishUser selectFishUserByOpenid(String openid);
    public java.util.List<FishUser> selectFishUserList(FishUser user);
    public FishUser loginOrRegister(String openid, String nickname, String avatar);
    public int updateFishUser(FishUser user);

    /** 校验用户是否被拉黑，被拉黑则抛出 ServiceException，附带原因 */
    public void assertNotBlacklisted(Long userId);

    /** 设置/解除黑名单 */
    public int setBlacklist(Long userId, boolean blacklist, String reason);
}
