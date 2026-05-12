package com.ruoyi.fishing.mapper;

import org.apache.ibatis.annotations.Param;
import com.ruoyi.fishing.domain.FishUserBalance;

public interface FishUserBalanceMapper
{
    FishUserBalance selectByUserId(@Param("userId") Long userId);

    /** 初始化空记录（不存在则插入），存在则忽略 */
    int insertIfAbsent(@Param("userId") Long userId);

    /**
     * 原子加减余额，并维护累计统计：
     *   delta > 0 入账（recharge=true 时累计充值），
     *   delta < 0 扣减（同时累计消费）。
     * 扣减时 SQL 内含 balance + delta >= 0 条件，余额不足返回 0。
     */
    int applyDelta(@Param("userId") Long userId,
                   @Param("delta") Integer delta,
                   @Param("recharge") Boolean recharge);
}
