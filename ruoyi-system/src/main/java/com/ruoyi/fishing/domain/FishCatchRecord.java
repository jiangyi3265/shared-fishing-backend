package com.ruoyi.fishing.domain;

import java.math.BigDecimal;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

public class FishCatchRecord extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long catchId;
    private Long userId;
    private String nickname;
    private String avatar;
    private Long venueId;
    private Long orderId;

    @Excel(name = "鱼种")
    private String fishSpecies;

    @Excel(name = "重量(斤)")
    private BigDecimal weightJin;

    private Integer fishCount;
    private String images;
    private String content;
    private String fishingMethod;

    @Excel(name = "精选", readConverterExp = "0=普通,1=精选")
    private Integer isFeatured;

    private Integer likeCount;
    private Integer commentCount;

    @Excel(name = "状态", readConverterExp = "0=待审核,1=已通过,2=已拒绝")
    private Integer status;

    private String rejectReason;
    private String delFlag;

    // 前端辅助
    private Boolean liked;

    public Long getCatchId() { return catchId; }
    public void setCatchId(Long catchId) { this.catchId = catchId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }
    public Long getVenueId() { return venueId; }
    public void setVenueId(Long venueId) { this.venueId = venueId; }
    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
    public String getFishSpecies() { return fishSpecies; }
    public void setFishSpecies(String fishSpecies) { this.fishSpecies = fishSpecies; }
    public BigDecimal getWeightJin() { return weightJin; }
    public void setWeightJin(BigDecimal weightJin) { this.weightJin = weightJin; }
    public Integer getFishCount() { return fishCount; }
    public void setFishCount(Integer fishCount) { this.fishCount = fishCount; }
    public String getImages() { return images; }
    public void setImages(String images) { this.images = images; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getFishingMethod() { return fishingMethod; }
    public void setFishingMethod(String fishingMethod) { this.fishingMethod = fishingMethod; }
    public Integer getIsFeatured() { return isFeatured; }
    public void setIsFeatured(Integer isFeatured) { this.isFeatured = isFeatured; }
    public Integer getLikeCount() { return likeCount; }
    public void setLikeCount(Integer likeCount) { this.likeCount = likeCount; }
    public Integer getCommentCount() { return commentCount; }
    public void setCommentCount(Integer commentCount) { this.commentCount = commentCount; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public String getRejectReason() { return rejectReason; }
    public void setRejectReason(String rejectReason) { this.rejectReason = rejectReason; }
    public String getDelFlag() { return delFlag; }
    public void setDelFlag(String delFlag) { this.delFlag = delFlag; }
    public Boolean getLiked() { return liked; }
    public void setLiked(Boolean liked) { this.liked = liked; }
}
