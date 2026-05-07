package com.ruoyi.fishing.mapper;

import java.util.List;
import com.ruoyi.fishing.domain.FishAd;

public interface FishAdMapper
{
    public FishAd selectFishAdByAdId(Long adId);
    public List<FishAd> selectFishAdList(FishAd ad);
    public int insertFishAd(FishAd ad);
    public int updateFishAd(FishAd ad);
    public int deleteFishAdByAdId(Long adId);
    public int deleteFishAdByAdIds(Long[] adIds);
}
