package com.ruoyi.fishing.domain;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

public class FishMemberLevel extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long levelId;

    @Excel(name = "等级名称")
    private String levelName;

    private String levelIcon;

    @Excel(name = "消费门槛(分)")
    private Integer minConsumeCents;

    @Excel(name = "折扣(%)")
    private Integer discountRate;

    private Integer freeDeposit;
    private Integer priorityReserve;
    private String extraBenefits;
    private Integer sortNum;
    private String status;

    public Long getLevelId() { return levelId; }
    public void setLevelId(Long levelId) { this.levelId = levelId; }
    public String getLevelName() { return levelName; }
    public void setLevelName(String levelName) { this.levelName = levelName; }
    public String getLevelIcon() { return levelIcon; }
    public void setLevelIcon(String levelIcon) { this.levelIcon = levelIcon; }
    public Integer getMinConsumeCents() { return minConsumeCents; }
    public void setMinConsumeCents(Integer minConsumeCents) { this.minConsumeCents = minConsumeCents; }
    public Integer getDiscountRate() { return discountRate; }
    public void setDiscountRate(Integer discountRate) { this.discountRate = discountRate; }
    public Integer getFreeDeposit() { return freeDeposit; }
    public void setFreeDeposit(Integer freeDeposit) { this.freeDeposit = freeDeposit; }
    public Integer getPriorityReserve() { return priorityReserve; }
    public void setPriorityReserve(Integer priorityReserve) { this.priorityReserve = priorityReserve; }
    public String getExtraBenefits() { return extraBenefits; }
    public void setExtraBenefits(String extraBenefits) { this.extraBenefits = extraBenefits; }
    public Integer getSortNum() { return sortNum; }
    public void setSortNum(Integer sortNum) { this.sortNum = sortNum; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
