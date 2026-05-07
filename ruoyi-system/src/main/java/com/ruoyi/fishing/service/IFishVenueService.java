package com.ruoyi.fishing.service;

import java.util.List;
import com.ruoyi.fishing.domain.FishVenue;

public interface IFishVenueService
{
    public FishVenue selectFishVenueByVenueId(Long venueId);
    public List<FishVenue> selectFishVenueList(FishVenue venue);
    public int insertFishVenue(FishVenue venue);
    public int updateFishVenue(FishVenue venue);
    public int deleteFishVenueByVenueIds(Long[] venueIds);
    public int deleteFishVenueByVenueId(Long venueId);
}
