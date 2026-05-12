package com.ruoyi.fishing.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

public class FishRechargeOrder extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long rechargeId;

    @Excel(name = "充值单号")
    private String rechargeNo;

    @Excel(name = "用户ID")
    private Long userId;

    private Long planId;

    @Excel(name = "支付金额(分)")
    private Integer amountCents;

    @Excel(name = "赠送(分)")
    private Integer bonusCents;

    @Excel(name = "入账金额(分)")
    private Integer totalCreditCents;

    @Excel(name = "状态", readConverterExp = "0=待支付,1=已完成,2=已取消")
    private Integer status;

    private String payTradeNo;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date paidTime;

    private String nickname; // 联表

    public Long getRechargeId() { return rechargeId; }
    public void setRechargeId(Long rechargeId) { this.rechargeId = rechargeId; }
    public String getRechargeNo() { return rechargeNo; }
    public void setRechargeNo(String rechargeNo) { this.rechargeNo = rechargeNo; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getPlanId() { return planId; }
    public void setPlanId(Long planId) { this.planId = planId; }
    public Integer getAmountCents() { return amountCents; }
    public void setAmountCents(Integer amountCents) { this.amountCents = amountCents; }
    public Integer getBonusCents() { return bonusCents; }
    public void setBonusCents(Integer bonusCents) { this.bonusCents = bonusCents; }
    public Integer getTotalCreditCents() { return totalCreditCents; }
    public void setTotalCreditCents(Integer totalCreditCents) { this.totalCreditCents = totalCreditCents; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public String getPayTradeNo() { return payTradeNo; }
    public void setPayTradeNo(String payTradeNo) { this.payTradeNo = payTradeNo; }
    public Date getPaidTime() { return paidTime; }
    public void setPaidTime(Date paidTime) { this.paidTime = paidTime; }
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
}
