package com.ruoyi.fishing.domain;

import java.util.Date;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;

public class FishGroupFishing extends BaseEntity
{
    private static final long serialVersionUID = 1L;
    private Long groupId;
    private Long userId;
    private String nickname;
    private Long venueId;
    private Long spotId;
    private String spotName;
    private String title;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date fishingDate;
    private String timeSlot;
    private Integer maxMembers;
    private Integer currentCount;
    private String description;
    private Integer status;
    private List<FishGroupMember> members;

    public Long getGroupId() { return groupId; }
    public void setGroupId(Long groupId) { this.groupId = groupId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public Long getVenueId() { return venueId; }
    public void setVenueId(Long venueId) { this.venueId = venueId; }
    public Long getSpotId() { return spotId; }
    public void setSpotId(Long spotId) { this.spotId = spotId; }
    public String getSpotName() { return spotName; }
    public void setSpotName(String spotName) { this.spotName = spotName; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public Date getFishingDate() { return fishingDate; }
    public void setFishingDate(Date fishingDate) { this.fishingDate = fishingDate; }
    public String getTimeSlot() { return timeSlot; }
    public void setTimeSlot(String timeSlot) { this.timeSlot = timeSlot; }
    public Integer getMaxMembers() { return maxMembers; }
    public void setMaxMembers(Integer maxMembers) { this.maxMembers = maxMembers; }
    public Integer getCurrentCount() { return currentCount; }
    public void setCurrentCount(Integer currentCount) { this.currentCount = currentCount; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public List<FishGroupMember> getMembers() { return members; }
    public void setMembers(List<FishGroupMember> members) { this.members = members; }
}
