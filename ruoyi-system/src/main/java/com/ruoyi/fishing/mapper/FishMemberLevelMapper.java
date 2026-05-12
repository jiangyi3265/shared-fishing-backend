package com.ruoyi.fishing.mapper;

import java.util.List;
import com.ruoyi.fishing.domain.FishMemberLevel;

public interface FishMemberLevelMapper
{
    FishMemberLevel selectById(Long levelId);
    List<FishMemberLevel> selectList(FishMemberLevel query);
    List<FishMemberLevel> selectAllActive();
    int insert(FishMemberLevel level);
    int update(FishMemberLevel level);
    int deleteByIds(Long[] levelIds);
}
