package com.ruoyi.fishing.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.fishing.domain.FishRegistration;

public interface FishRegistrationMapper
{
    public FishRegistration selectFishRegistrationByRegId(Long regId);
    public FishRegistration selectByAdAndUser(@Param("adId") Long adId, @Param("userId") Long userId);
    public List<FishRegistration> selectFishRegistrationList(FishRegistration reg);
    public List<FishRegistration> selectByUserId(@Param("userId") Long userId);
    public int insertFishRegistration(FishRegistration reg);
    public int updateFishRegistration(FishRegistration reg);
    public int deleteFishRegistrationByRegId(Long regId);
    public int deleteFishRegistrationByRegIds(Long[] regIds);
}
