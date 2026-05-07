package com.ruoyi.fishing.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.fishing.domain.FishBillingRule;
import com.ruoyi.fishing.mapper.FishBillingRuleMapper;
import com.ruoyi.fishing.service.IFishBillingRuleService;

@Service
public class FishBillingRuleServiceImpl implements IFishBillingRuleService
{
    @Autowired
    private FishBillingRuleMapper ruleMapper;

    @Override
    public FishBillingRule selectFishBillingRuleByRuleId(Long ruleId) { return ruleMapper.selectFishBillingRuleByRuleId(ruleId); }

    @Override
    public List<FishBillingRule> selectFishBillingRuleList(FishBillingRule rule) { return ruleMapper.selectFishBillingRuleList(rule); }

    @Override
    public int insertFishBillingRule(FishBillingRule rule)
    {
        rule.setCreateBy(safeUser());
        rule.setCreateTime(DateUtils.getNowDate());
        return ruleMapper.insertFishBillingRule(rule);
    }

    @Override
    public int updateFishBillingRule(FishBillingRule rule)
    {
        rule.setUpdateBy(safeUser());
        rule.setUpdateTime(DateUtils.getNowDate());
        return ruleMapper.updateFishBillingRule(rule);
    }

    @Override
    public int deleteFishBillingRuleByRuleIds(Long[] ruleIds) { return ruleMapper.deleteFishBillingRuleByRuleIds(ruleIds); }

    @Override
    public int deleteFishBillingRuleByRuleId(Long ruleId) { return ruleMapper.deleteFishBillingRuleByRuleId(ruleId); }

    private String safeUser() {
        try { return SecurityUtils.getUsername(); } catch (Exception e) { return "system"; }
    }
}
