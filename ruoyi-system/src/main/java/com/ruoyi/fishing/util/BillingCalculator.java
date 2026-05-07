package com.ruoyi.fishing.util;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.fishing.domain.FishBillingRule;

/**
 * 计费工具
 * 金额统一"分"
 *
 * 支持三种模式：
 * 1. 固定单价：step + price
 * 2. 分时段：time_segment_json = [{"start":"18:00","end":"06:00","pricePerStepCents":600}, ...]
 * 3. 进位方式（roundType）：ceil_minute / ceil_step / ceil_hour
 */
public class BillingCalculator
{
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static class Result
    {
        public int amountCents;
        public int billableDurationSeconds;
        public int elapsedSeconds;
    }

    public static Result calc(FishBillingRule rule, long startMillis, long endMillis)
    {
        Result r = new Result();
        long elapsedMs = Math.max(0L, endMillis - startMillis);
        int totalSeconds = (int) (elapsedMs / 1000L);
        r.elapsedSeconds = totalSeconds;

        int stepMinutes = nullSafe(rule.getStepMinutes(), 30);
        int minMinutes = nullSafe(rule.getMinDurationMinutes(), stepMinutes);
        int defaultPricePerStep = nullSafe(rule.getPricePerStepCents(), 0);
        int cap = nullSafe(rule.getCapAmountCents(), 0);
        int stepSeconds = Math.max(1, stepMinutes) * 60;
        int minSeconds = Math.max(0, minMinutes) * 60;

        int billable = Math.max(minSeconds, roundUp(totalSeconds, stepSeconds, rule.getRoundType()));
        r.billableDurationSeconds = billable;

        List<Segment> segments = parseSegments(rule.getTimeSegmentJson());
        long amount;
        if (segments.isEmpty())
        {
            amount = ((long) billable / stepSeconds) * defaultPricePerStep;
        }
        else
        {
            amount = calcWithSegments(startMillis, billable, stepSeconds, defaultPricePerStep, segments);
        }

        if (cap > 0 && amount > cap) amount = cap;
        r.amountCents = (int) amount;
        return r;
    }

    private static int roundUp(int seconds, int stepSeconds, String roundType)
    {
        if (seconds <= 0) return 0;
        if ("ceil_minute".equals(roundType)) return ((seconds + 59) / 60) * 60;
        if ("ceil_hour".equals(roundType)) return ((seconds + 3599) / 3600) * 3600;
        return ((seconds + stepSeconds - 1) / stepSeconds) * stepSeconds;
    }

    /**
     * 分时段计费：把计费时长切成若干 step，按 step 起始时刻落在哪个时段选单价
     * 简化模型：时段可跨天（start > end 表示跨 0 点）
     */
    private static long calcWithSegments(long startMillis, int billableSeconds, int stepSeconds,
                                         int defaultPricePerStep, List<Segment> segments)
    {
        long amount = 0;
        int stepCount = billableSeconds / stepSeconds;
        for (int i = 0; i < stepCount; i++)
        {
            long stepStartMs = startMillis + (long) i * stepSeconds * 1000L;
            LocalTime stepStart = LocalDateTime.ofInstant(new Date(stepStartMs).toInstant(),
                    java.time.ZoneId.systemDefault()).toLocalTime();
            int price = priceFor(stepStart, segments, defaultPricePerStep);
            amount += price;
        }
        return amount;
    }

    private static int priceFor(LocalTime t, List<Segment> segments, int fallback)
    {
        for (Segment s : segments)
        {
            if (s.contains(t)) return s.pricePerStepCents;
        }
        return fallback;
    }

    private static List<Segment> parseSegments(String json)
    {
        List<Segment> list = new ArrayList<>();
        if (json == null || json.trim().isEmpty()) return list;
        try
        {
            JsonNode arr = MAPPER.readTree(json);
            if (!arr.isArray()) return list;
            for (JsonNode n : arr)
            {
                Segment s = new Segment();
                s.start = LocalTime.parse(n.path("start").asText("00:00"));
                s.end = LocalTime.parse(n.path("end").asText("00:00"));
                s.pricePerStepCents = n.path("pricePerStepCents").asInt(0);
                list.add(s);
            }
        }
        catch (Exception ignore) { /* json 异常退回默认单价 */ }
        return list;
    }

    private static int nullSafe(Integer v, int def) { return v == null ? def : v; }

    private static class Segment
    {
        LocalTime start;
        LocalTime end;
        int pricePerStepCents;

        boolean contains(LocalTime t)
        {
            if (start.equals(end)) return true;
            if (start.isBefore(end)) return !t.isBefore(start) && t.isBefore(end);
            return !t.isBefore(start) || t.isBefore(end);
        }
    }
}
