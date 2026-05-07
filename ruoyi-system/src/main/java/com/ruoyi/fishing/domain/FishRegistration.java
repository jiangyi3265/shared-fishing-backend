package com.ruoyi.fishing.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 活动报名 fish_registration
 */
public class FishRegistration extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long regId;

    @Excel(name = "活动ID")
    private Long adId;

    @Excel(name = "用户ID")
    private Long userId;

    @Excel(name = "姓名")
    private String name;

    @Excel(name = "电话")
    private String phone;

    private String remark2;
    private Integer feeCents;
    private Integer paid;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date paidTime;

    private Integer status;
    private String activityName;

    public Long getRegId() { return regId; }
    public void setRegId(Long regId) { this.regId = regId; }
    public Long getAdId() { return adId; }
    public void setAdId(Long adId) { this.adId = adId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getRemark2() { return remark2; }
    public void setRemark2(String remark2) { this.remark2 = remark2; }
    public Integer getFeeCents() { return feeCents; }
    public void setFeeCents(Integer feeCents) { this.feeCents = feeCents; }
    public Integer getPaid() { return paid; }
    public void setPaid(Integer paid) { this.paid = paid; }
    public Date getPaidTime() { return paidTime; }
    public void setPaidTime(Date paidTime) { this.paidTime = paidTime; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public String getActivityName() { return activityName; }
    public void setActivityName(String activityName) { this.activityName = activityName; }
}
