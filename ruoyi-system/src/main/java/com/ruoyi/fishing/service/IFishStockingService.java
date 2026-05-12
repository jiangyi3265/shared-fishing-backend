package com.ruoyi.fishing.service;

import java.util.List;
import com.ruoyi.fishing.domain.FishStockingRecord;

public interface IFishStockingService
{
    FishStockingRecord selectByRecordId(Long recordId);
    List<FishStockingRecord> selectList(FishStockingRecord query);
    List<FishStockingRecord> selectPublicByVenue(Long venueId);
    int insertRecord(FishStockingRecord record);
    int updateRecord(FishStockingRecord record);
    int deleteByRecordIds(Long[] recordIds);
}
