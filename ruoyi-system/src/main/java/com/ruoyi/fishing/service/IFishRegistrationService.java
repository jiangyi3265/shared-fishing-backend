package com.ruoyi.fishing.service;

import java.util.List;
import com.ruoyi.fishing.domain.FishRegistration;

public interface IFishRegistrationService
{
    public FishRegistration selectFishRegistrationByRegId(Long regId);
    public List<FishRegistration> selectFishRegistrationList(FishRegistration reg);
    public List<FishRegistration> selectByUserId(Long userId);
    public FishRegistration submit(Long adId, Long userId, String name, String phone, String remark);
    public FishRegistration pay(Long regId);
    public int updateFishRegistration(FishRegistration reg);
    public int deleteFishRegistrationByRegIds(Long[] regIds);
}
