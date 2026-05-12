package com.ruoyi.fishing.service;

import java.util.List;
import com.ruoyi.fishing.domain.FishCompetition;
import com.ruoyi.fishing.domain.FishCompetitionEntry;

public interface IFishCompetitionService
{
    FishCompetition selectById(Long compId);
    List<FishCompetition> selectList(FishCompetition query);
    List<FishCompetition> selectActiveList(Long venueId);
    int insert(FishCompetition comp);
    int update(FishCompetition comp);
    int updateStatus(Long compId, int status);

    // 报名
    FishCompetitionEntry enter(Long compId, Long userId, String nickname, String phone);
    List<FishCompetitionEntry> selectEntries(Long compId);
    List<FishCompetitionEntry> selectRanking(Long compId);

    // 称重(店员)
    int weigh(Long entryId, int weightGram, int fishCount, String weighBy, String weighImage);

    // 结算排名+发奖
    int settle(Long compId);
}
