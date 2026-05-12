package com.ruoyi.fishing.domain;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

public class FishRechargePlan extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long planId;

    @Excel(name = "套餐名")
    private String title;

    @Excel(name = "支付金额(分)")
    private Integer amountCents;

    @Excel(name = "赠送金额(分)")
    private Integer bonusCents;

    private String badge;
    private Integer sort;

    @Excel(name = "状态", readConverterExp = "0=上架,1=下架")
    private String status;

    public Long getPlanId() { return planId; }
    public void setPlanId(Long planId) { this.planId = planId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public Integer getAmountCents() { return amountCents; }
    public void setAmountCents(Integer amountCents) { this.amountCents = amountCents; }
    public Integer getBonusCents() { return bonusCents; }
    public void setBonusCents(Integer bonusCents) { this.bonusCents = bonusCents; }
    public String getBadge() { return badge; }
    public void setBadge(String badge) { this.badge = badge; }
    public Integer getSort() { return sort; }
    public void setSort(Integer sort) { this.sort = sort; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
