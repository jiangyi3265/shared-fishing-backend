package com.ruoyi.fishing.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.fishing.domain.FishAd;
import com.ruoyi.fishing.mapper.FishAdMapper;
import com.ruoyi.fishing.service.IFishAdService;

@Service
public class FishAdServiceImpl implements IFishAdService
{
    @Autowired
    private FishAdMapper adMapper;

    @Override
    public FishAd selectFishAdByAdId(Long adId) { return adMapper.selectFishAdByAdId(adId); }

    @Override
    public List<FishAd> selectFishAdList(FishAd ad) { return adMapper.selectFishAdList(ad); }

    @Override
    public int insertFishAd(FishAd ad)
    {
        ad.setCreateBy(safeUser());
        ad.setCreateTime(DateUtils.getNowDate());
        return adMapper.insertFishAd(ad);
    }

    @Override
    public int updateFishAd(FishAd ad)
    {
        ad.setUpdateBy(safeUser());
        ad.setUpdateTime(DateUtils.getNowDate());
        return adMapper.updateFishAd(ad);
    }

    @Override
    public int deleteFishAdByAdIds(Long[] adIds) { return adMapper.deleteFishAdByAdIds(adIds); }

    @Override
    public int deleteFishAdByAdId(Long adId) { return adMapper.deleteFishAdByAdId(adId); }

    private String safeUser() {
        try { return SecurityUtils.getUsername(); } catch (Exception e) { return "system"; }
    }
}
