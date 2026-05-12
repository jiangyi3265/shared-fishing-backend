package com.ruoyi.fishing.mapper;

import java.util.List;
import com.ruoyi.fishing.domain.FishStockingRecord;

public interface FishStockingRecordMapper
{
    FishStockingRecord selectByRecordId(Long recordId);
    List<FishStockingRecord> selectList(FishStockingRecord query);
    List<FishStockingRecord> selectPublicByVenue(Long venueId);
    int insertRecord(FishStockingRecord record);
    int updateRecord(FishStockingRecord record);
    int deleteByRecordId(Long recordId);
    int deleteByRecordIds(Long[] recordIds);
}
