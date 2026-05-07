package com.ruoyi.fishing.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 计费规则 fish_billing_rule
 */
public class FishBillingRule extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long ruleId;

    @Excel(name = "规则名称")
    private String ruleName;

    private String unitType;
    private Integer stepMinutes;
    private Integer pricePerStepCents;
    private Integer minDurationMinutes;
    private String roundType;
    private Integer capAmountCents;
    private Integer dailyCapCents;
    private String timeSegmentJson;
    private String summary;

    @Excel(name = "状态", readConverterExp = "0=正常,1=停用")
    private String status;

    private String delFlag;

    public Long getRuleId() { return ruleId; }
    public void setRuleId(Long ruleId) { this.ruleId = ruleId; }
    public String getRuleName() { return ruleName; }
    public void setRuleName(String ruleName) { this.ruleName = ruleName; }
    public String getUnitType() { return unitType; }
    public void setUnitType(String unitType) { this.unitType = unitType; }
    public Integer getStepMinutes() { return stepMinutes; }
    public void setStepMinutes(Integer stepMinutes) { this.stepMinutes = stepMinutes; }
    public Integer getPricePerStepCents() { return pricePerStepCents; }
    public void setPricePerStepCents(Integer pricePerStepCents) { this.pricePerStepCents = pricePerStepCents; }
    public Integer getMinDurationMinutes() { return minDurationMinutes; }
    public void setMinDurationMinutes(Integer minDurationMinutes) { this.minDurationMinutes = minDurationMinutes; }
    public String getRoundType() { return roundType; }
    public void setRoundType(String roundType) { this.roundType = roundType; }
    public Integer getCapAmountCents() { return capAmountCents; }
    public void setCapAmountCents(Integer capAmountCents) { this.capAmountCents = capAmountCents; }
    public Integer getDailyCapCents() { return dailyCapCents; }
    public void setDailyCapCents(Integer dailyCapCents) { this.dailyCapCents = dailyCapCents; }
    public String getTimeSegmentJson() { return timeSegmentJson; }
    public void setTimeSegmentJson(String timeSegmentJson) { this.timeSegmentJson = timeSegmentJson; }
    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getDelFlag() { return delFlag; }
    public void setDelFlag(String delFlag) { this.delFlag = delFlag; }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("ruleId", ruleId).append("ruleName", ruleName).append("unitType", unitType)
            .append("stepMinutes", stepMinutes).append("pricePerStepCents", pricePerStepCents)
            .append("minDurationMinutes", minDurationMinutes).append("roundType", roundType)
            .append("capAmountCents", capAmountCents).append("status", status).toString();
    }
}
