package com.ruoyi.fishing.mapper;

import java.util.List;
import com.ruoyi.fishing.domain.FishVenue;

public interface FishVenueMapper
{
    public FishVenue selectFishVenueByVenueId(Long venueId);
    public List<FishVenue> selectFishVenueList(FishVenue venue);
    public int insertFishVenue(FishVenue venue);
    public int updateFishVenue(FishVenue venue);
    public int deleteFishVenueByVenueId(Long venueId);
    public int deleteFishVenueByVenueIds(Long[] venueIds);
}
