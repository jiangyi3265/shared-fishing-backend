package com.ruoyi.fishing.domain;

import java.util.Date;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 称鱼结算订单 fish_weigh_order
 */
public class FishWeighOrder extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long fishWeighId;

    @Excel(name = "称鱼单号")
    private String weighNo;

    @Excel(name = "用户ID")
    private Long userId;

    @Excel(name = "钓场ID")
    private Long venueId;

    /** 鱼获重量(克) */
    @Excel(name = "重量(克)")
    private Integer weightGrams;

    /** 结算单价(分/斤)，下单时快照 */
    @Excel(name = "单价(分/斤)")
    private Integer priceCents;

    /** 是否会员价 0否 1是 */
    @Excel(name = "会员价", readConverterExp = "0=否,1=是")
    private Integer isMember;

    @Excel(name = "应付金额(分)")
    private Integer amountCents;

    /** 0待支付 1已完成 2已取消 */
    @Excel(name = "状态", readConverterExp = "0=待支付,1=已完成,2=已取消")
    private Integer status;

    private String payTradeNo;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date paidTime;

    // 联表展示
    private String nickname;
    private String venueName;

    public Long getFishWeighId() { return fishWeighId; }
    public void setFishWeighId(Long fishWeighId) { this.fishWeighId = fishWeighId; }
    public String getWeighNo() { return weighNo; }
    public void setWeighNo(String weighNo) { this.weighNo = weighNo; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getVenueId() { return venueId; }
    public void setVenueId(Long venueId) { this.venueId = venueId; }
    public Integer getWeightGrams() { return weightGrams; }
    public void setWeightGrams(Integer weightGrams) { this.weightGrams = weightGrams; }
    public Integer getPriceCents() { return priceCents; }
    public void setPriceCents(Integer priceCents) { this.priceCents = priceCents; }
    public Integer getIsMember() { return isMember; }
    public void setIsMember(Integer isMember) { this.isMember = isMember; }
    public Integer getAmountCents() { return amountCents; }
    public void setAmountCents(Integer amountCents) { this.amountCents = amountCents; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public String getPayTradeNo() { return payTradeNo; }
    public void setPayTradeNo(String payTradeNo) { this.payTradeNo = payTradeNo; }
    public Date getPaidTime() { return paidTime; }
    public void setPaidTime(Date paidTime) { this.paidTime = paidTime; }
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public String getVenueName() { return venueName; }
    public void setVenueName(String venueName) { this.venueName = venueName; }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("fishWeighId", fishWeighId).append("weighNo", weighNo).append("userId", userId)
            .append("venueId", venueId).append("weightGrams", weightGrams).append("priceCents", priceCents)
            .append("amountCents", amountCents).append("status", status).toString();
    }
}
