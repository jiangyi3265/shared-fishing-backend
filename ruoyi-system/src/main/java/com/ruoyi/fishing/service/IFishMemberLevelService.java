package com.ruoyi.fishing.service;

import java.util.List;
import com.ruoyi.fishing.domain.FishMemberLevel;

public interface IFishMemberLevelService
{
    FishMemberLevel selectById(Long levelId);
    List<FishMemberLevel> selectList(FishMemberLevel query);
    List<FishMemberLevel> selectAllActive();
    int insert(FishMemberLevel level);
    int update(FishMemberLevel level);
    int deleteByIds(Long[] levelIds);

    /** 根据累计消费计算用户应属等级，并更新 fish_user */
    void refreshUserLevel(Long userId);

    /** 获取用户当前折扣率(百分比) */
    int getUserDiscountRate(Long userId);
}
