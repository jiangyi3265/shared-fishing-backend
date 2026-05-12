package com.ruoyi.fishing.domain;

import java.math.BigDecimal;
import java.util.Date;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 放鱼记录 fish_stocking_record
 */
public class FishStockingRecord extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long recordId;

    @Excel(name = "钓场ID")
    private Long venueId;

    private String venueName;

    @Excel(name = "鱼种")
    private String fishSpecies;

    @Excel(name = "斤数")
    private BigDecimal weightJin;

    @Excel(name = "尾数")
    private Integer fishCount;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "放鱼时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date stockingTime;

    private String image;
    private String content;

    @Excel(name = "状态", readConverterExp = "0=已发布,1=隐藏")
    private String status;

    private String delFlag;

    public Long getRecordId() { return recordId; }
    public void setRecordId(Long recordId) { this.recordId = recordId; }
    public Long getVenueId() { return venueId; }
    public void setVenueId(Long venueId) { this.venueId = venueId; }
    public String getVenueName() { return venueName; }
    public void setVenueName(String venueName) { this.venueName = venueName; }
    public String getFishSpecies() { return fishSpecies; }
    public void setFishSpecies(String fishSpecies) { this.fishSpecies = fishSpecies; }
    public BigDecimal getWeightJin() { return weightJin; }
    public void setWeightJin(BigDecimal weightJin) { this.weightJin = weightJin; }
    public Integer getFishCount() { return fishCount; }
    public void setFishCount(Integer fishCount) { this.fishCount = fishCount; }
    public Date getStockingTime() { return stockingTime; }
    public void setStockingTime(Date stockingTime) { this.stockingTime = stockingTime; }
    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getDelFlag() { return delFlag; }
    public void setDelFlag(String delFlag) { this.delFlag = delFlag; }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("recordId", recordId).append("venueId", venueId)
            .append("fishSpecies", fishSpecies).append("weightJin", weightJin)
            .append("stockingTime", stockingTime).append("status", status).toString();
    }
}
