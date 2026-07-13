package com.ruoyi.fishing.service.impl;

import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.fishing.domain.FishStockingRecord;
import com.ruoyi.fishing.domain.FishVenue;
import com.ruoyi.fishing.mapper.FishStockingRecordMapper;
import com.ruoyi.fishing.mapper.FishVenueMapper;
import com.ruoyi.fishing.service.IFishStockingService;

@Service
public class FishStockingServiceImpl implements IFishStockingService
{
    @Autowired
    private FishStockingRecordMapper mapper;

    @Autowired
    private FishVenueMapper venueMapper;

    @Override
    public FishStockingRecord selectByRecordId(Long recordId) { return mapper.selectByRecordId(recordId); }

    @Override
    public List<FishStockingRecord> selectList(FishStockingRecord query) { return mapper.selectList(query); }

    @Override
    public List<FishStockingRecord> selectPublicByVenue(Long venueId) { return mapper.selectPublicByVenue(venueId); }

    @Override
    public int insertRecord(FishStockingRecord record)
    {
        validateRecord(record);
        validateVenue(record.getVenueId());
        if (record.getStockingTime() == null) record.setStockingTime(new Date());
        if (record.getStatus() == null) record.setStatus("0");
        record.setCreateBy(safeUser());
        record.setCreateTime(DateUtils.getNowDate());
        return mapper.insertRecord(record);
    }

    @Override
    public int updateRecord(FishStockingRecord record)
    {
        if (record == null || record.getRecordId() == null)
        {
            throw new ServiceException("放鱼记录ID不能为空");
        }
        validateRecord(record);
        if (record.getVenueId() != null) validateVenue(record.getVenueId());
        record.setUpdateBy(safeUser());
        record.setUpdateTime(DateUtils.getNowDate());
        return mapper.updateRecord(record);
    }

    @Override
    public int deleteByRecordIds(Long[] recordIds) { return mapper.deleteByRecordIds(recordIds); }

    private String safeUser() {
        try { return SecurityUtils.getUsername(); } catch (Exception e) { return "system"; }
    }

    private void validateVenue(Long venueId)
    {
        if (venueId == null) throw new ServiceException("请选择所属钓场");
        FishVenue venue = venueMapper.selectFishVenueByVenueId(venueId);
        if (venue == null) throw new ServiceException("所属钓场不存在");
        if ("1".equals(venue.getStatus())) throw new ServiceException("所属钓场已停用");
    }

    private void validateRecord(FishStockingRecord record)
    {
        if (record == null) throw new ServiceException("放鱼记录不能为空");
        String fishSpecies = record.getFishSpecies();
        if (fishSpecies == null || fishSpecies.trim().isEmpty())
        {
            throw new ServiceException("请填写鱼种");
        }
        record.setFishSpecies(fishSpecies.trim());
        if (record.getWeightJin() == null || record.getWeightJin().signum() <= 0)
        {
            throw new ServiceException("放鱼斤数必须大于 0");
        }
        if (record.getFishCount() != null && record.getFishCount() < 0)
        {
            throw new ServiceException("放鱼尾数不能小于 0");
        }
    }
}
