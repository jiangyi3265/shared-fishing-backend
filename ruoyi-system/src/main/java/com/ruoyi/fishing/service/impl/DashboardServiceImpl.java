package com.ruoyi.fishing.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.fishing.mapper.FishDashboardMapper;
import com.ruoyi.fishing.service.IDashboardService;

@Service
public class DashboardServiceImpl implements IDashboardService
{
    @Autowired
    private FishDashboardMapper dashboardMapper;

    @Override
    public Map<String, Object> summary()
    {
        Map<String, Object> data = new HashMap<>();
        Map<String, Object> today = emptyIfNull(dashboardMapper.summaryToday());
        Map<String, Object> yesterday = emptyIfNull(dashboardMapper.summaryYesterday());
        Map<String, Object> month = emptyIfNull(dashboardMapper.summaryMonth());
        Map<String, Object> active = emptyIfNull(dashboardMapper.activeStats());
        data.put("today", today);
        data.put("yesterday", yesterday);
        data.put("month", month);
        data.put("active", active);

        long todayRev = toLong(today.get("revenueCents"));
        long yestRev = toLong(yesterday.get("revenueCents"));
        double growth = yestRev == 0 ? (todayRev > 0 ? 1.0 : 0.0) : (todayRev - yestRev) * 1.0 / yestRev;
        data.put("revenueGrowth", growth);

        long total = toLong(today.get("orderCount"));
        long paid = toLong(today.get("paidCount"));
        data.put("payConversion", total == 0 ? 0.0 : paid * 1.0 / total);
        return data;
    }

    @Override
    public List<Map<String, Object>> orderTrend(int days) { return dashboardMapper.orderTrend(clampDays(days)); }

    @Override
    public List<Map<String, Object>> revenueTrend(int days) { return dashboardMapper.revenueTrend(clampDays(days)); }

    private int clampDays(int days) { return days < 1 ? 7 : Math.min(days, 90); }
    private Map<String, Object> emptyIfNull(Map<String, Object> m) { return m == null ? new HashMap<>() : m; }
    private long toLong(Object v) { return v == null ? 0L : ((Number) v).longValue(); }
}
