package com.ruoyi.fishing.domain;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

public class FishSpot extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long spotId;

    @Excel(name = "钓场ID")
    private Long venueId;

    private String venueName;

    @Excel(name = "钓位名称")
    private String spotName;

    @Excel(name = "类型")
    private String spotType;

    @Excel(name = "附加费(分)")
    private Integer extraFeeCents;

    private Integer capacity;
    private Integer sortNum;

    @Excel(name = "状态", readConverterExp = "0=可用,1=维护中,2=停用")
    private String status;

    private String description;
    private String delFlag;

    public Long getSpotId() { return spotId; }
    public void setSpotId(Long spotId) { this.spotId = spotId; }
    public Long getVenueId() { return venueId; }
    public void setVenueId(Long venueId) { this.venueId = venueId; }
    public String getVenueName() { return venueName; }
    public void setVenueName(String venueName) { this.venueName = venueName; }
    public String getSpotName() { return spotName; }
    public void setSpotName(String spotName) { this.spotName = spotName; }
    public String getSpotType() { return spotType; }
    public void setSpotType(String spotType) { this.spotType = spotType; }
    public Integer getExtraFeeCents() { return extraFeeCents; }
    public void setExtraFeeCents(Integer extraFeeCents) { this.extraFeeCents = extraFeeCents; }
    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }
    public Integer getSortNum() { return sortNum; }
    public void setSortNum(Integer sortNum) { this.sortNum = sortNum; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getDelFlag() { return delFlag; }
    public void setDelFlag(String delFlag) { this.delFlag = delFlag; }
}
