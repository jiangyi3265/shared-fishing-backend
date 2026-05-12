package com.ruoyi.fishing.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.fishing.domain.FishRechargeOrder;

public interface FishRechargeOrderMapper
{
    FishRechargeOrder selectById(Long rechargeId);
    FishRechargeOrder selectByRechargeNo(@Param("rechargeNo") String rechargeNo);
    List<FishRechargeOrder> selectList(FishRechargeOrder q);
    List<FishRechargeOrder> selectByUser(@Param("userId") Long userId);
    int insert(FishRechargeOrder o);
    int update(FishRechargeOrder o);
    int updateStatusWithGuard(@Param("rechargeId") Long rechargeId,
                              @Param("expectedStatus") Integer expectedStatus,
                              @Param("newStatus") Integer newStatus);
}
