package com.ruoyi.fishing.service;

import java.util.List;
import com.ruoyi.fishing.domain.FishAd;

public interface IFishAdService
{
    public FishAd selectFishAdByAdId(Long adId);
    public List<FishAd> selectFishAdList(FishAd ad);
    public int insertFishAd(FishAd ad);
    public int updateFishAd(FishAd ad);
    public int deleteFishAdByAdIds(Long[] adIds);
    public int deleteFishAdByAdId(Long adId);
}
