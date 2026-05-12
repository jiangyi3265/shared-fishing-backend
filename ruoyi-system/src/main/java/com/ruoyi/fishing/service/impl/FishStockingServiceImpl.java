package com.ruoyi.fishing.service.impl;

import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.fishing.domain.FishStockingRecord;
import com.ruoyi.fishing.mapper.FishStockingRecordMapper;
import com.ruoyi.fishing.service.IFishStockingService;

@Service
public class FishStockingServiceImpl implements IFishStockingService
{
    @Autowired
    private FishStockingRecordMapper mapper;

    @Override
    public FishStockingRecord selectByRecordId(Long recordId) { return mapper.selectByRecordId(recordId); }

    @Override
    public List<FishStockingRecord> selectList(FishStockingRecord query) { return mapper.selectList(query); }

    @Override
    public List<FishStockingRecord> selectPublicByVenue(Long venueId) { return mapper.selectPublicByVenue(venueId); }

    @Override
    public int insertRecord(FishStockingRecord record)
    {
        if (record.getStockingTime() == null) record.setStockingTime(new Date());
        if (record.getStatus() == null) record.setStatus("0");
        record.setCreateBy(safeUser());
        record.setCreateTime(DateUtils.getNowDate());
        return mapper.insertRecord(record);
    }

    @Override
    public int updateRecord(FishStockingRecord record)
    {
        record.setUpdateBy(safeUser());
        record.setUpdateTime(DateUtils.getNowDate());
        return mapper.updateRecord(record);
    }

    @Override
    public int deleteByRecordIds(Long[] recordIds) { return mapper.deleteByRecordIds(recordIds); }

    private String safeUser() {
        try { return SecurityUtils.getUsername(); } catch (Exception e) { return "system"; }
    }
}
