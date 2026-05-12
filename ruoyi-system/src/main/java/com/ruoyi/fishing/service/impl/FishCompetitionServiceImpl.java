package com.ruoyi.fishing.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.fishing.domain.FishCompetition;
import com.ruoyi.fishing.domain.FishCompetitionEntry;
import com.ruoyi.fishing.mapper.FishCompetitionMapper;
import com.ruoyi.fishing.service.IFishBalanceService;
import com.ruoyi.fishing.service.IFishCompetitionService;
import com.ruoyi.fishing.service.IFishUserService;

@Service
public class FishCompetitionServiceImpl implements IFishCompetitionService
{
    @Autowired private FishCompetitionMapper mapper;
    @Autowired private IFishUserService userService;
    @Autowired private IFishBalanceService balanceService;

    @Override public FishCompetition selectById(Long compId) {
        FishCompetition c = mapper.selectById(compId);
        if (c != null) c.setEntries(mapper.selectEntries(compId));
        return c;
    }
    @Override public List<FishCompetition> selectList(FishCompetition q) { return mapper.selectList(q); }
    @Override public List<FishCompetition> selectActiveList(Long venueId) { return mapper.selectActiveList(venueId); }
    @Override public int insert(FishCompetition c) { c.setStatus(0); try{c.setCreateBy(SecurityUtils.getUsername());}catch(Exception e){} return mapper.insert(c); }
    @Override public int update(FishCompetition c) { return mapper.update(c); }
    @Override public int updateStatus(Long compId, int status) { return mapper.updateStatus(compId, status); }

    @Override
    @Transactional
    public FishCompetitionEntry enter(Long compId, Long userId, String nickname, String phone) {
        userService.assertNotBlacklisted(userId);
        FishCompetition c = mapper.selectById(compId);
        if (c == null) throw new ServiceException("比赛不存在");
        if (c.getStatus() != 0) throw new ServiceException("报名已截止");

        FishCompetitionEntry existing = mapper.selectEntry(compId, userId);
        if (existing != null) return existing;

        if (c.getMaxPlayers() > 0 && mapper.countEntries(compId) >= c.getMaxPlayers())
            throw new ServiceException("报名人数已满");

        // 收取报名费（从余额扣除）
        if (c.getEntryFeeCents() != null && c.getEntryFeeCents() > 0) {
            balanceService.applyDelta(userId, -c.getEntryFeeCents(),
                "consume_fishing", "COMP" + compId, "比赛报名费", "system");
        }

        FishCompetitionEntry e = new FishCompetitionEntry();
        e.setCompId(compId);
        e.setUserId(userId);
        e.setNickname(nickname);
        e.setPhone(phone);
        mapper.insertEntry(e);
        return e;
    }

    @Override public List<FishCompetitionEntry> selectEntries(Long compId) { return mapper.selectEntries(compId); }
    @Override public List<FishCompetitionEntry> selectRanking(Long compId) { return mapper.selectRanking(compId); }

    @Override
    public int weigh(Long entryId, int weightGram, int fishCount, String weighBy, String weighImage) {
        return mapper.updateEntryWeigh(entryId, weightGram, fishCount, weighBy, weighImage);
    }

    @Override
    @Transactional
    public int settle(Long compId) {
        List<FishCompetitionEntry> ranked = mapper.selectRanking(compId);
        FishCompetition c = mapper.selectById(compId);
        String prizeJson = c.getPrizeRules();

        int rank = 1;
        for (FishCompetitionEntry e : ranked) {
            int prize = parsePrize(prizeJson, rank);
            mapper.updateEntryRanking(e.getEntryId(), rank, prize);
            if (prize > 0) {
                balanceService.adminAdjust(e.getUserId(), prize, "比赛奖金#" + compId + " 第" + rank + "名", "system");
                mapper.updateEntryPrizeStatus(e.getEntryId(), 1);
            }
            rank++;
        }
        mapper.updateStatus(compId, 3);
        return ranked.size();
    }

    private int parsePrize(String json, int rank) {
        if (json == null || json.isEmpty()) return 0;
        try {
            String search = "\"rank\":" + rank;
            int idx = json.indexOf(search);
            if (idx < 0) return 0;
            int amtIdx = json.indexOf("\"amount\":", idx);
            if (amtIdx < 0) return 0;
            int start = amtIdx + 9;
            int end = start;
            while (end < json.length() && (Character.isDigit(json.charAt(end)))) end++;
            return Integer.parseInt(json.substring(start, end));
        } catch (Exception e) { return 0; }
    }
}
