package com.ruoyi.fishing.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 广告/活动 fish_ad
 */
public class FishAd extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long adId;

    @Excel(name = "类型")
    private String adType;

    @Excel(name = "标题")
    private String title;

    private String description;
    private String image;
    private String bgColor;
    private String content;
    private Integer sortNum;

    private String activityName;
    private String activityDate;
    private String activityLocation;
    private Integer activityFeeCents;
    private Integer activitySlots;
    private String activityRules;

    private Long couponTemplateId;

    @Excel(name = "状态", readConverterExp = "0=正常,1=停用")
    private String status;

    private String delFlag;

    public Long getAdId() { return adId; }
    public void setAdId(Long adId) { this.adId = adId; }
    public String getAdType() { return adType; }
    public void setAdType(String adType) { this.adType = adType; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
    public String getBgColor() { return bgColor; }
    public void setBgColor(String bgColor) { this.bgColor = bgColor; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public Integer getSortNum() { return sortNum; }
    public void setSortNum(Integer sortNum) { this.sortNum = sortNum; }
    public String getActivityName() { return activityName; }
    public void setActivityName(String activityName) { this.activityName = activityName; }
    public String getActivityDate() { return activityDate; }
    public void setActivityDate(String activityDate) { this.activityDate = activityDate; }
    public String getActivityLocation() { return activityLocation; }
    public void setActivityLocation(String activityLocation) { this.activityLocation = activityLocation; }
    public Integer getActivityFeeCents() { return activityFeeCents; }
    public void setActivityFeeCents(Integer activityFeeCents) { this.activityFeeCents = activityFeeCents; }
    public Integer getActivitySlots() { return activitySlots; }
    public void setActivitySlots(Integer activitySlots) { this.activitySlots = activitySlots; }
    public String getActivityRules() { return activityRules; }
    public void setActivityRules(String activityRules) { this.activityRules = activityRules; }
    public Long getCouponTemplateId() { return couponTemplateId; }
    public void setCouponTemplateId(Long couponTemplateId) { this.couponTemplateId = couponTemplateId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getDelFlag() { return delFlag; }
    public void setDelFlag(String delFlag) { this.delFlag = delFlag; }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("adId", adId).append("adType", adType).append("title", title)
            .append("status", status).toString();
    }
}
