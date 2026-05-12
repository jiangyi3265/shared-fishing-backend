package com.ruoyi.fishing.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.fishing.domain.FishSpot;

public interface FishSpotMapper
{
    FishSpot selectById(Long spotId);
    List<FishSpot> selectList(FishSpot query);
    List<FishSpot> selectAvailableByVenue(Long venueId);
    int insert(FishSpot spot);
    int update(FishSpot spot);
    int deleteByIds(Long[] spotIds);
}
