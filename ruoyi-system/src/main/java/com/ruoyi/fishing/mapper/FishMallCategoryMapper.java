package com.ruoyi.fishing.mapper;

import java.util.List;
import com.ruoyi.fishing.domain.FishMallCategory;

public interface FishMallCategoryMapper
{
    FishMallCategory selectById(Long catId);
    List<FishMallCategory> selectList(FishMallCategory cat);
    int insert(FishMallCategory cat);
    int update(FishMallCategory cat);
    int deleteByIds(Long[] catIds);
}
