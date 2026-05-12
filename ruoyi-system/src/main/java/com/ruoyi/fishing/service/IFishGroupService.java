package com.ruoyi.fishing.service;

import java.util.List;
import com.ruoyi.fishing.domain.FishGroupFishing;

public interface IFishGroupService
{
    FishGroupFishing selectById(Long groupId);
    List<FishGroupFishing> selectList(FishGroupFishing query);
    List<FishGroupFishing> selectActiveList(Long venueId);
    List<FishGroupFishing> selectByUser(Long userId);
    FishGroupFishing create(FishGroupFishing g);
    int join(Long groupId, Long userId);
    int quit(Long groupId, Long userId);
    int cancel(Long groupId, Long userId);
}
