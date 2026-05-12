package com.ruoyi.fishing.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.fishing.domain.FishMemberLevel;
import com.ruoyi.fishing.domain.FishUser;
import com.ruoyi.fishing.domain.FishUserBalance;
import com.ruoyi.fishing.mapper.FishMemberLevelMapper;
import com.ruoyi.fishing.mapper.FishUserBalanceMapper;
import com.ruoyi.fishing.mapper.FishUserMapper;
import com.ruoyi.fishing.service.IFishMemberLevelService;

@Service
public class FishMemberLevelServiceImpl implements IFishMemberLevelService
{
    @Autowired private FishMemberLevelMapper levelMapper;
    @Autowired private FishUserMapper userMapper;
    @Autowired private FishUserBalanceMapper balanceMapper;

    @Override public FishMemberLevel selectById(Long levelId) { return levelMapper.selectById(levelId); }
    @Override public List<FishMemberLevel> selectList(FishMemberLevel query) { return levelMapper.selectList(query); }
    @Override public List<FishMemberLevel> selectAllActive() { return levelMapper.selectAllActive(); }
    @Override public int insert(FishMemberLevel level) { if (level.getStatus() == null) level.setStatus("0"); return levelMapper.insert(level); }
    @Override public int update(FishMemberLevel level) { return levelMapper.update(level); }
    @Override public int deleteByIds(Long[] levelIds) { return levelMapper.deleteByIds(levelIds); }

    @Override
    public void refreshUserLevel(Long userId)
    {
        FishUserBalance bal = balanceMapper.selectByUserId(userId);
        int consumed = bal != null ? bal.getTotalConsumedCents() : 0;

        List<FishMemberLevel> levels = levelMapper.selectAllActive();
        FishMemberLevel matched = null;
        for (FishMemberLevel l : levels) {
            if (consumed >= l.getMinConsumeCents()) matched = l;
        }

        FishUser u = new FishUser();
        u.setUserId(userId);
        if (matched != null) {
            u.setMemberLevelId(matched.getLevelId());
            u.setMemberLevelName(matched.getLevelName());
        } else {
            u.setMemberLevelId(null);
            u.setMemberLevelName("");
        }
        userMapper.updateFishUser(u);
    }

    @Override
    public int getUserDiscountRate(Long userId)
    {
        FishUser u = userMapper.selectFishUserByUserId(userId);
        if (u == null || u.getMemberLevelId() == null) return 100;
        FishMemberLevel level = levelMapper.selectById(u.getMemberLevelId());
        return level != null ? level.getDiscountRate() : 100;
    }
}
