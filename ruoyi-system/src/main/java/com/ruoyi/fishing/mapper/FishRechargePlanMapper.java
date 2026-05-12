package com.ruoyi.fishing.mapper;

import java.util.List;
import com.ruoyi.fishing.domain.FishRechargePlan;

public interface FishRechargePlanMapper
{
    FishRechargePlan selectById(Long planId);
    List<FishRechargePlan> selectList(FishRechargePlan q);
    int insert(FishRechargePlan p);
    int update(FishRechargePlan p);
    int deleteByIds(Long[] planIds);
}
