package com.ruoyi.fishing.service.impl;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.exception.ServiceException;
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
        validateCoordinates(venue);
        venue.setCreateBy(safeUser());
        venue.setCreateTime(DateUtils.getNowDate());
        return venueMapper.insertFishVenue(venue);
    }

    @Override
    public int updateFishVenue(FishVenue venue)
    {
        validateCoordinates(venue);
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

    private void validateCoordinates(FishVenue venue)
    {
        if (venue == null) throw new ServiceException("钓场信息不能为空");
        BigDecimal latitude = venue.getLatitude();
        BigDecimal longitude = venue.getLongitude();
        if ((latitude == null) != (longitude == null))
        {
            throw new ServiceException("经纬度必须同时填写或同时清空");
        }
        if (latitude != null && latitude.compareTo(BigDecimal.ZERO) == 0
                && longitude.compareTo(BigDecimal.ZERO) == 0)
        {
            throw new ServiceException("经纬度不能同时为 0");
        }
        if (latitude != null
                && (latitude.compareTo(new BigDecimal("-90")) < 0
                || latitude.compareTo(new BigDecimal("90")) > 0))
        {
            throw new ServiceException("纬度必须在 -90 到 90 之间");
        }
        if (longitude != null
                && (longitude.compareTo(new BigDecimal("-180")) < 0
                || longitude.compareTo(new BigDecimal("180")) > 0))
        {
            throw new ServiceException("经度必须在 -180 到 180 之间");
        }
    }
}
