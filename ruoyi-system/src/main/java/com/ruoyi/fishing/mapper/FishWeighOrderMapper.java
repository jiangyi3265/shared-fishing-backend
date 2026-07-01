package com.ruoyi.fishing.mapper;

import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.fishing.domain.FishWeighOrder;

public interface FishWeighOrderMapper
{
    /** 钓王榜：按当月累计钓获重量排名（venueId 为空则全场） */
    List<Map<String, Object>> selectWeightRanking(@Param("venueId") Long venueId);

    FishWeighOrder selectById(Long fishWeighId);
    FishWeighOrder selectByWeighNo(@Param("weighNo") String weighNo);
    List<FishWeighOrder> selectList(FishWeighOrder q);
    List<FishWeighOrder> selectByUser(@Param("userId") Long userId);
    int insert(FishWeighOrder o);
    int update(FishWeighOrder o);
    int updateStatusWithGuard(@Param("fishWeighId") Long fishWeighId,
                              @Param("expectedStatus") Integer expectedStatus,
                              @Param("newStatus") Integer newStatus);
}
