package com.ruoyi.fishing.service;

import java.util.List;
import java.util.Map;
import com.ruoyi.fishing.domain.FishCatchRecord;

public interface IFishCardGameService
{
    Map<String, Object> getMyGame(Long userId);
    FishCatchRecord submit(Long userId, Long speciesId, String videoUrl);
    void syncCatchAudit(Long catchId, int status);
    List<Map<String, Object>> selectAdminRounds(Map<String, Object> query);
    int markRewardPaid(Long roundId, String operator);
}
