package com.ruoyi.fishing.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

public class FishUserBalance
{
    private Long userId;
    private Integer balanceCents;
    private Integer totalRechargeCents;
    private Integer totalConsumedCents;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Integer getBalanceCents() { return balanceCents; }
    public void setBalanceCents(Integer balanceCents) { this.balanceCents = balanceCents; }
    public Integer getTotalRechargeCents() { return totalRechargeCents; }
    public void setTotalRechargeCents(Integer totalRechargeCents) { this.totalRechargeCents = totalRechargeCents; }
    public Integer getTotalConsumedCents() { return totalConsumedCents; }
    public void setTotalConsumedCents(Integer totalConsumedCents) { this.totalConsumedCents = totalConsumedCents; }
    public Date getUpdateTime() { return updateTime; }
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }
}
