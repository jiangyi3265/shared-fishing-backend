package com.ruoyi.fishing.service;

import java.util.List;
import java.util.Map;

public interface IDashboardService
{
    /** 今日/昨日/本月 汇总 */
    Map<String, Object> summary();

    /** 近 N 天订单趋势 */
    List<Map<String, Object>> orderTrend(int days);

    /** 近 N 天营收趋势 */
    List<Map<String, Object>> revenueTrend(int days);
}
