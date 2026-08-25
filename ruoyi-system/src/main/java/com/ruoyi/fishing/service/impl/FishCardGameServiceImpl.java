package com.ruoyi.fishing.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.fishing.domain.FishCatchRecord;
import com.ruoyi.fishing.mapper.FishCardGameMapper;
import com.ruoyi.fishing.mapper.FishCatchRecordMapper;
import com.ruoyi.fishing.service.IFishCardGameService;
import com.ruoyi.fishing.service.IFishUserService;

@Service
public class FishCardGameServiceImpl implements IFishCardGameService
{
    @Autowired private FishCardGameMapper mapper;
    @Autowired private FishCatchRecordMapper catchMapper;
    @Autowired private IFishUserService userService;

    @Override
    public Map<String, Object> getMyGame(Long userId)
    {
        Map<String, Object> campaign = mapper.selectActiveCampaign();
        if (campaign == null) throw new ServiceException("当前没有开放中的鱼卡活动");
        Long campaignId = asLong(campaign.get("campaignId"));
        List<Map<String, Object>> species = mapper.selectSpecies(campaignId);
        List<Map<String, Object>> displaySpecies = mapper.selectSpeciesForDisplay(campaignId);
        if (displaySpecies == null || displaySpecies.isEmpty()) displaySpecies = species;
        Map<String, Object> round = mapper.selectOpenRound(campaignId, userId);

        int roundNo = 1;
        int completedRounds = 0;
        Long roundId = null;
        List<Map<String, Object>> progress = new ArrayList<>();
        if (round != null)
        {
            roundId = asLong(round.get("roundId"));
            roundNo = asInt(round.get("roundNo"), 1);
            completedRounds = Math.max(0, roundNo - 1);
            progress = mapper.selectProgressList(roundId);
        }
        else
        {
            Integer max = mapper.selectMaxRoundNo(campaignId, userId);
            completedRounds = max == null ? 0 : max;
            roundNo = completedRounds + 1;
            round = new HashMap<>();
            round.put("roundId", null);
            round.put("roundNo", roundNo);
            round.put("status", 0);
        }

        int obtained = 0;
        int pending = 0;
        List<Map<String, Object>> cards = new ArrayList<>();
        for (Map<String, Object> fish : displaySpecies)
        {
            Map<String, Object> card = new HashMap<>(fish);
            boolean available = isActiveSpecies(species, asLong(fish.get("speciesId")));
            Map<String, Object> hit = available ? findProgress(progress, asLong(fish.get("speciesId"))) : null;
            String cardStatus = available ? "locked" : "unavailable";
            if (available && hit != null)
            {
                int status = asInt(hit.get("status"), 0);
                if (status == 1) { cardStatus = "obtained"; obtained++; }
                else if (status == 2) cardStatus = "rejected";
                else { cardStatus = "pending"; pending++; }
                card.putAll(hit);
            }
            card.put("available", available);
            card.put("cardStatus", cardStatus);
            cards.add(card);
        }

        Date start = normalizeCampaignDate(campaign.get("startTime"));
        Date end = normalizeCampaignDate(campaign.get("endTime"));
        Date now = new Date();
        String phase = now.before(start) ? "upcoming" : (now.after(end) ? "ended" : "active");

        round.put("roundNo", roundNo);
        round.put("completedRounds", completedRounds);
        round.put("obtainedCount", obtained);
        round.put("pendingCount", pending);
        round.put("totalCount", species.size());

        Map<String, Object> result = new HashMap<>();
        result.put("campaign", campaign);
        result.put("phase", phase);
        result.put("round", round);
        result.put("cards", cards);
        result.put("ranking", mapper.selectRanking(campaignId));
        return result;
    }

    private boolean isActiveSpecies(List<Map<String, Object>> species, Long speciesId)
    {
        if (species == null || speciesId == null) return false;
        for (Map<String, Object> fish : species)
        {
            Long activeId = asLong(fish.get("speciesId"));
            if (speciesId.equals(activeId)) return true;
        }
        return false;
    }

    @Override
    @Transactional
    public FishCatchRecord submit(Long userId, Long speciesId, String videoUrl)
    {
        if (videoUrl == null || videoUrl.trim().isEmpty()) throw new ServiceException("请上传钓获及放流全过程视频");
        userService.assertNotBlacklisted(userId);

        Map<String, Object> campaign = mapper.selectActiveCampaign();
        if (campaign == null) throw new ServiceException("当前没有开放中的鱼卡活动");
        Date now = new Date();
        Date start = normalizeCampaignDate(campaign.get("startTime"));
        Date end = normalizeCampaignDate(campaign.get("endTime"));
        if (now.before(start)) throw new ServiceException("活动尚未开始");
        if (now.after(end)) throw new ServiceException("活动已结束");

        Long campaignId = asLong(campaign.get("campaignId"));
        Map<String, Object> species = mapper.selectSpeciesById(campaignId, speciesId);
        if (species == null) throw new ServiceException("鱼种不在本次鱼鉴范围内");

        Map<String, Object> round = mapper.selectOpenRound(campaignId, userId);
        if (round == null)
        {
            Integer max = mapper.selectMaxRoundNo(campaignId, userId);
            int next = (max == null ? 0 : max) + 1;
            mapper.insertRound(campaignId, userId, next, asInt(campaign.get("rewardCents"), 6600));
            round = mapper.selectOpenRound(campaignId, userId);
        }
        if (round == null) throw new ServiceException("本轮鱼鉴创建失败，请重试");

        Long roundId = asLong(round.get("roundId"));
        Map<String, Object> existing = mapper.selectProgress(roundId, speciesId);
        if (existing != null)
        {
            int status = asInt(existing.get("status"), 0);
            if (status == 0) throw new ServiceException("该鱼卡正在审核中");
            if (status == 1) throw new ServiceException("本轮已经获得这张鱼卡");
        }

        FishCatchRecord record = new FishCatchRecord();
        record.setUserId(userId);
        record.setVenueId(asLong(campaign.get("venueId")));
        record.setFishSpecies(String.valueOf(species.get("speciesName")));
        record.setVideoUrl(videoUrl.trim());
        record.setContent("极智鱼鉴 · 龙水湖篇，第" + asInt(round.get("roundNo"), 1) + "轮鱼卡认证");
        record.setFishingMethod("电子鱼卡");
        record.setCardRoundId(roundId);
        record.setCardSpeciesId(speciesId);
        record.setStatus(0);
        catchMapper.insert(record);
        mapper.upsertProgress(roundId, speciesId, record.getCatchId());
        return record;
    }

    @Override
    @Transactional
    public void syncCatchAudit(Long catchId, int status)
    {
        Map<String, Object> progress = mapper.selectProgressByCatch(catchId);
        if (progress == null) return;
        int cardStatus = status == 1 ? 1 : 2;
        int changed = mapper.updateProgressByCatch(catchId, cardStatus);
        if (changed == 0 || status != 1) return;

        Long roundId = asLong(progress.get("roundId"));
        Map<String, Object> round = mapper.selectRoundById(roundId);
        if (round == null || asInt(round.get("status"), 0) != 0) return;
        Long campaignId = asLong(round.get("campaignId"));
        int total = mapper.selectSpecies(campaignId).size();
        if (total > 0 && mapper.countApproved(roundId) >= total) mapper.completeRound(roundId);
    }

    @Override
    public List<Map<String, Object>> selectAdminRounds(Map<String, Object> query)
    {
        return mapper.selectAdminRounds(query == null ? new HashMap<String, Object>() : query);
    }

    @Override
    @Transactional
    public int markRewardPaid(Long roundId, String operator)
    {
        Map<String, Object> round = mapper.selectRoundById(roundId);
        if (round == null) throw new ServiceException("鱼鉴轮次不存在");
        if (asInt(round.get("status"), 0) != 1) throw new ServiceException("本轮尚未集齐");
        if (asInt(round.get("rewardStatus"), 0) == 1) return 1;
        return mapper.markRewardPaid(roundId, operator == null ? "" : operator);
    }

    private Map<String, Object> findProgress(List<Map<String, Object>> list, Long speciesId)
    {
        if (list == null) return null;
        for (Map<String, Object> item : list)
        {
            Long current = asLong(item.get("speciesId"));
            if (current != null && current.equals(speciesId)) return item;
        }
        return null;
    }

    private Long asLong(Object value)
    {
        if (value == null) return null;
        if (value instanceof Number) return ((Number) value).longValue();
        return Long.valueOf(String.valueOf(value));
    }

    private int asInt(Object value, int fallback)
    {
        if (value == null) return fallback;
        if (value instanceof Number) return ((Number) value).intValue();
        try { return Integer.parseInt(String.valueOf(value)); }
        catch (Exception e) { return fallback; }
    }

    /**
     * MyBatis/JDBC 驱动可能把 DATETIME 返回为 Date，也可能返回为 LocalDateTime。
     * 统一转换后再参与活动时间判断，避免线上 ClassCastException。
     */
    static Date normalizeCampaignDate(Object value)
    {
        if (value instanceof Date) return (Date) value;
        if (value instanceof LocalDateTime)
        {
            LocalDateTime time = (LocalDateTime) value;
            return Date.from(time.atZone(ZoneId.systemDefault()).toInstant());
        }
        if (value instanceof LocalDate)
        {
            LocalDate date = (LocalDate) value;
            return Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
        }
        throw new ServiceException("鱼卡活动时间配置异常");
    }
}
