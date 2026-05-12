package com.ruoyi.fishing.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;

public class FishBalanceLog
{
    public static final String TYPE_RECHARGE        = "recharge";
    public static final String TYPE_GIFT            = "gift";
    public static final String TYPE_CONSUME_FISHING = "consume_fishing";
    public static final String TYPE_CONSUME_MALL    = "consume_mall";
    public static final String TYPE_REFUND          = "refund";
    public static final String TYPE_ADMIN_ADJUST    = "admin_adjust";

    private Long logId;

    @Excel(name = "用户ID")
    private Long userId;

    @Excel(name = "变动金额(分)")
    private Integer deltaCents;

    private Integer balanceAfterCents;

    @Excel(name = "类型")
    private String type;

    private String relatedOrderNo;
    private Long relatedRechargeId;
    private String remark;
    private String operator;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    private String nickname; // 联表

    public Long getLogId() { return logId; }
    public void setLogId(Long logId) { this.logId = logId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Integer getDeltaCents() { return deltaCents; }
    public void setDeltaCents(Integer deltaCents) { this.deltaCents = deltaCents; }
    public Integer getBalanceAfterCents() { return balanceAfterCents; }
    public void setBalanceAfterCents(Integer balanceAfterCents) { this.balanceAfterCents = balanceAfterCents; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getRelatedOrderNo() { return relatedOrderNo; }
    public void setRelatedOrderNo(String relatedOrderNo) { this.relatedOrderNo = relatedOrderNo; }
    public Long getRelatedRechargeId() { return relatedRechargeId; }
    public void setRelatedRechargeId(Long relatedRechargeId) { this.relatedRechargeId = relatedRechargeId; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
    public String getOperator() { return operator; }
    public void setOperator(String operator) { this.operator = operator; }
    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
}
