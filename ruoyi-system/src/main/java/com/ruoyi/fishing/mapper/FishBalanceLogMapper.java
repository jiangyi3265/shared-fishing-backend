package com.ruoyi.fishing.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.fishing.domain.FishBalanceLog;

public interface FishBalanceLogMapper
{
    int insert(FishBalanceLog log);
    List<FishBalanceLog> selectList(FishBalanceLog q);
    List<FishBalanceLog> selectByUser(@Param("userId") Long userId);
}
