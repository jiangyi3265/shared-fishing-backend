package com.ruoyi.fishing.mapper;

import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;

public interface FishCardGameMapper
{
    Map<String, Object> selectActiveCampaign();
    Map<String, Object> selectCampaignById(Long campaignId);
    /** 当前可参与并计入集齐奖励的鱼种。 */
    List<Map<String, Object>> selectSpecies(Long campaignId);
    /** 鱼鉴页面展示卡片：可参与鱼种优先，其余显示为待解锁，最多十张。 */
    List<Map<String, Object>> selectSpeciesForDisplay(Long campaignId);
    Map<String, Object> selectSpeciesById(@Param("campaignId") Long campaignId,
                                          @Param("speciesId") Long speciesId);

    Map<String, Object> selectOpenRound(@Param("campaignId") Long campaignId,
                                        @Param("userId") Long userId);
    Map<String, Object> selectRoundById(Long roundId);
    Integer selectMaxRoundNo(@Param("campaignId") Long campaignId, @Param("userId") Long userId);
    int insertRound(@Param("campaignId") Long campaignId, @Param("userId") Long userId,
                    @Param("roundNo") int roundNo, @Param("rewardCents") int rewardCents);

    Map<String, Object> selectProgress(@Param("roundId") Long roundId,
                                       @Param("speciesId") Long speciesId);
    Map<String, Object> selectProgressByCatch(Long catchId);
    List<Map<String, Object>> selectProgressList(Long roundId);
    int upsertProgress(@Param("roundId") Long roundId, @Param("speciesId") Long speciesId,
                       @Param("catchId") Long catchId);
    int updateProgressByCatch(@Param("catchId") Long catchId, @Param("status") int status);
    int countApproved(Long roundId);
    int completeRound(Long roundId);

    List<Map<String, Object>> selectRanking(Long campaignId);
    List<Map<String, Object>> selectAdminRounds(Map<String, Object> query);
    int markRewardPaid(@Param("roundId") Long roundId, @Param("operator") String operator);
}
