package com.ruoyi.fishing.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

public class FishCompetitionEntry
{
    private Long entryId;
    private Long compId;
    private Long userId;
    private String nickname;
    private String phone;
    private Integer weightGram;
    private Integer fishCount;
    private Integer ranking;
    private Integer prizeCents;
    private Integer prizeStatus;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date weighTime;
    private String weighBy;
    private String weighImage;
    private Integer status;
    private Date createTime;

    public Long getEntryId() { return entryId; }
    public void setEntryId(Long entryId) { this.entryId = entryId; }
    public Long getCompId() { return compId; }
    public void setCompId(Long compId) { this.compId = compId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public Integer getWeightGram() { return weightGram; }
    public void setWeightGram(Integer weightGram) { this.weightGram = weightGram; }
    public Integer getFishCount() { return fishCount; }
    public void setFishCount(Integer fishCount) { this.fishCount = fishCount; }
    public Integer getRanking() { return ranking; }
    public void setRanking(Integer ranking) { this.ranking = ranking; }
    public Integer getPrizeCents() { return prizeCents; }
    public void setPrizeCents(Integer prizeCents) { this.prizeCents = prizeCents; }
    public Integer getPrizeStatus() { return prizeStatus; }
    public void setPrizeStatus(Integer prizeStatus) { this.prizeStatus = prizeStatus; }
    public Date getWeighTime() { return weighTime; }
    public void setWeighTime(Date weighTime) { this.weighTime = weighTime; }
    public String getWeighBy() { return weighBy; }
    public void setWeighBy(String weighBy) { this.weighBy = weighBy; }
    public String getWeighImage() { return weighImage; }
    public void setWeighImage(String weighImage) { this.weighImage = weighImage; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
}
