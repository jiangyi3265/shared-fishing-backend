package com.ruoyi.fishing.domain;

import java.util.Date;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;

public class FishCompetition extends BaseEntity
{
    private static final long serialVersionUID = 1L;
    private Long compId;
    private Long adId;
    private Long venueId;
    private String title;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date compDate;
    private String timeSlot;
    private Integer maxPlayers;
    private Integer entryFeeCents;
    private Integer prizePoolCents;
    private String prizeRules;
    private String fishSpecies;
    private String rules;
    private Integer status;
    private Integer entryCount;
    private List<FishCompetitionEntry> entries;

    public Long getCompId() { return compId; }
    public void setCompId(Long compId) { this.compId = compId; }
    public Long getAdId() { return adId; }
    public void setAdId(Long adId) { this.adId = adId; }
    public Long getVenueId() { return venueId; }
    public void setVenueId(Long venueId) { this.venueId = venueId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public Date getCompDate() { return compDate; }
    public void setCompDate(Date compDate) { this.compDate = compDate; }
    public String getTimeSlot() { return timeSlot; }
    public void setTimeSlot(String timeSlot) { this.timeSlot = timeSlot; }
    public Integer getMaxPlayers() { return maxPlayers; }
    public void setMaxPlayers(Integer maxPlayers) { this.maxPlayers = maxPlayers; }
    public Integer getEntryFeeCents() { return entryFeeCents; }
    public void setEntryFeeCents(Integer entryFeeCents) { this.entryFeeCents = entryFeeCents; }
    public Integer getPrizePoolCents() { return prizePoolCents; }
    public void setPrizePoolCents(Integer prizePoolCents) { this.prizePoolCents = prizePoolCents; }
    public String getPrizeRules() { return prizeRules; }
    public void setPrizeRules(String prizeRules) { this.prizeRules = prizeRules; }
    public String getFishSpecies() { return fishSpecies; }
    public void setFishSpecies(String fishSpecies) { this.fishSpecies = fishSpecies; }
    public String getRules() { return rules; }
    public void setRules(String rules) { this.rules = rules; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public Integer getEntryCount() { return entryCount; }
    public void setEntryCount(Integer entryCount) { this.entryCount = entryCount; }
    public List<FishCompetitionEntry> getEntries() { return entries; }
    public void setEntries(List<FishCompetitionEntry> entries) { this.entries = entries; }
}
