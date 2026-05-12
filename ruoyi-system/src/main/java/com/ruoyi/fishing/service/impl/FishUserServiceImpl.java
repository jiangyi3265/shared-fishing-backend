package com.ruoyi.fishing.service.impl;

import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.fishing.domain.FishUser;
import com.ruoyi.fishing.mapper.FishUserMapper;
import com.ruoyi.fishing.service.IFishUserService;

@Service
public class FishUserServiceImpl implements IFishUserService
{
    @Autowired
    private FishUserMapper userMapper;

    @Override
    public FishUser selectFishUserByUserId(Long userId) { return userMapper.selectFishUserByUserId(userId); }

    @Override
    public FishUser selectFishUserByOpenid(String openid) { return userMapper.selectFishUserByOpenid(openid); }

    @Override
    public List<FishUser> selectFishUserList(FishUser user) { return userMapper.selectFishUserList(user); }

    @Override
    public FishUser loginOrRegister(String openid, String nickname, String avatar)
    {
        FishUser existing = userMapper.selectFishUserByOpenid(openid);
        if (existing != null)
        {
            boolean dirty = false;
            if (nickname != null && !nickname.equals(existing.getNickname())) { existing.setNickname(nickname); dirty = true; }
            if (avatar != null && !avatar.equals(existing.getAvatar())) { existing.setAvatar(avatar); dirty = true; }
            existing.setLastLoginTime(DateUtils.getNowDate());
            dirty = true;
            if (dirty) userMapper.updateFishUser(existing);
            return existing;
        }
        FishUser u = new FishUser();
        u.setOpenid(openid);
        u.setNickname(nickname == null || nickname.isEmpty() ? "钓友_" + (System.currentTimeMillis() % 10000) : nickname);
        u.setAvatar(avatar == null ? "" : avatar);
        u.setStatus("0");
        u.setLastLoginTime(new Date());
        userMapper.insertFishUser(u);
        return u;
    }

    @Override
    public int updateFishUser(FishUser user) { return userMapper.updateFishUser(user); }

    @Override
    public void assertNotBlacklisted(Long userId)
    {
        FishUser u = userMapper.selectFishUserByUserId(userId);
        if (u != null && Integer.valueOf(1).equals(u.getIsBlacklist()))
        {
            String msg = "您已被限制使用";
            if (u.getBlacklistReason() != null && !u.getBlacklistReason().isEmpty())
                msg += "（" + u.getBlacklistReason() + "）";
            throw new ServiceException(msg);
        }
    }

    @Override
    public int setBlacklist(Long userId, boolean blacklist, String reason)
    {
        FishUser u = new FishUser();
        u.setUserId(userId);
        u.setIsBlacklist(blacklist ? 1 : 0);
        u.setBlacklistReason(blacklist ? (reason == null ? "" : reason) : "");
        return userMapper.updateFishUser(u);
    }
}
