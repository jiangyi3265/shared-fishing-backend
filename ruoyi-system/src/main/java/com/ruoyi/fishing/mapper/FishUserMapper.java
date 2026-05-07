package com.ruoyi.fishing.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.fishing.domain.FishUser;

public interface FishUserMapper
{
    public FishUser selectFishUserByUserId(Long userId);
    public FishUser selectFishUserByOpenid(@Param("openid") String openid);
    public List<FishUser> selectFishUserList(FishUser user);
    public int insertFishUser(FishUser user);
    public int updateFishUser(FishUser user);
    public int deleteFishUserByUserId(Long userId);
}
