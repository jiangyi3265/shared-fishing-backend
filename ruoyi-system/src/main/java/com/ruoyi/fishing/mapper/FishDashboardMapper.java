package com.ruoyi.fishing.mapper;

import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;

public interface FishDashboardMapper
{
    Map<String, Object> summaryToday();
    Map<String, Object> summaryYesterday();
    Map<String, Object> summaryMonth();
    List<Map<String, Object>> orderTrend(@Param("days") int days);
    List<Map<String, Object>> revenueTrend(@Param("days") int days);
    Map<String, Object> activeStats();
}
