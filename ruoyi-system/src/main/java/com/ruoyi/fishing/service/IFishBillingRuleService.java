package com.ruoyi.fishing.service;

import java.util.List;
import com.ruoyi.fishing.domain.FishBillingRule;

public interface IFishBillingRuleService
{
    public FishBillingRule selectFishBillingRuleByRuleId(Long ruleId);
    public List<FishBillingRule> selectFishBillingRuleList(FishBillingRule rule);
    public int insertFishBillingRule(FishBillingRule rule);
    public int updateFishBillingRule(FishBillingRule rule);
    public int deleteFishBillingRuleByRuleIds(Long[] ruleIds);
    public int deleteFishBillingRuleByRuleId(Long ruleId);
}
