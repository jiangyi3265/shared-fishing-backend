package com.ruoyi.fishing.domain;

import java.util.Date;

/**
 * 线上消费积分奖励。
 *
 * 状态：0 待发放，1 已到账。
 */
public class FishPointsReward
{
    private Long rewardId;
    private Long userId;
    private String sourceType;
    private String sourceNo;
    private Integer amountCents;
    private Integer points;
    private Integer status;
    private Date claimedTime;
    private Date createTime;

    public Long getRewardId() { return rewardId; }
    public void setRewardId(Long rewardId) { this.rewardId = rewardId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getSourceType() { return sourceType; }
    public void setSourceType(String sourceType) { this.sourceType = sourceType; }
    public String getSourceNo() { return sourceNo; }
    public void setSourceNo(String sourceNo) { this.sourceNo = sourceNo; }
    public Integer getAmountCents() { return amountCents; }
    public void setAmountCents(Integer amountCents) { this.amountCents = amountCents; }
    public Integer getPoints() { return points; }
    public void setPoints(Integer points) { this.points = points; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public Date getClaimedTime() { return claimedTime; }
    public void setClaimedTime(Date claimedTime) { this.claimedTime = claimedTime; }
    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
}
