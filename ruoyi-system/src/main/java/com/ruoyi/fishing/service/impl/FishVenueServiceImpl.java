package com.ruoyi.fishing.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.fishing.domain.FishVenue;
import com.ruoyi.fishing.mapper.FishVenueMapper;
import com.ruoyi.fishing.service.IFishVenueService;

@Service
public class FishVenueServiceImpl implements IFishVenueService
{
    @Autowired
    private FishVenueMapper venueMapper;

    @Override
    public FishVenue selectFishVenueByVenueId(Long venueId) { return venueMapper.selectFishVenueByVenueId(venueId); }

    @Override
    public List<FishVenue> selectFishVenueList(FishVenue venue) { return venueMapper.selectFishVenueList(venue); }

    @Override
    public int insertFishVenue(FishVenue venue)
    {
        venue.setCreateBy(safeUser());
        venue.setCreateTime(DateUtils.getNowDate());
        return venueMapper.insertFishVenue(venue);
    }

    @Override
    public int updateFishVenue(FishVenue venue)
    {
        venue.setUpdateBy(safeUser());
        venue.setUpdateTime(DateUtils.getNowDate());
        return venueMapper.updateFishVenue(venue);
    }

    @Override
    public int deleteFishVenueByVenueIds(Long[] venueIds) { return venueMapper.deleteFishVenueByVenueIds(venueIds); }

    @Override
    public int deleteFishVenueByVenueId(Long venueId) { return venueMapper.deleteFishVenueByVenueId(venueId); }

    private String safeUser() {
        try { return SecurityUtils.getUsername(); } catch (Exception e) { return "system"; }
    }
}
