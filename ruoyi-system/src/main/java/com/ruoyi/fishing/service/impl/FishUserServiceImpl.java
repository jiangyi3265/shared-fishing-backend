package com.ruoyi.fishing.service.impl;

import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
            if (dirty || true) userMapper.updateFishUser(existing);
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
}
