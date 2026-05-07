package com.ruoyi.fishing.mapper;

import java.util.List;
import com.ruoyi.fishing.domain.FishBillingRule;

public interface FishBillingRuleMapper
{
    public FishBillingRule selectFishBillingRuleByRuleId(Long ruleId);
    public List<FishBillingRule> selectFishBillingRuleList(FishBillingRule rule);
    public int insertFishBillingRule(FishBillingRule rule);
    public int updateFishBillingRule(FishBillingRule rule);
    public int deleteFishBillingRuleByRuleId(Long ruleId);
    public int deleteFishBillingRuleByRuleIds(Long[] ruleIds);
}
