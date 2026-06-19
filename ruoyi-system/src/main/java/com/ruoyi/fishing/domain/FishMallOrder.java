package com.ruoyi.fishing.domain;

import java.util.Date;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 商城订单 fish_mall_order
 *
 * 状态：0 待支付 → 1 可领取 → 2 已领取 ；0 → 3 已取消
 */
public class FishMallOrder extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long mallOrderId;

    @Excel(name = "订单号")
    private String mallOrderNo;

    @Excel(name = "用户ID")
    private Long userId;

    private Long venueId;

    @Excel(name = "合计(分)")
    private Integer totalCents;

    @Excel(name = "实付(分)")
    private Integer amountPaid;

    /** 使用余额抵扣(分) */
    private Integer balanceCents;

    /** 使用积分数（100 积分 = 1 元，1 积分 = 1 分） */
    private Integer pointsUsed;

    /** 积分抵扣金额(分) */
    private Integer pointsDeductCents;

    @Excel(name = "状态", readConverterExp = "0=待支付,1=可领取,2=已领取,3=已取消")
    private Integer status;

    private String remark2;
    private String redeemCode;
    private String payTradeNo;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date paidTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date redeemedTime;

    private String redeemedBy;
    private String nickname;
    private List<FishMallOrderItem> items;

    public Long getMallOrderId() { return mallOrderId; }
    public void setMallOrderId(Long mallOrderId) { this.mallOrderId = mallOrderId; }
    public String getMallOrderNo() { return mallOrderNo; }
    public void setMallOrderNo(String mallOrderNo) { this.mallOrderNo = mallOrderNo; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getVenueId() { return venueId; }
    public void setVenueId(Long venueId) { this.venueId = venueId; }
    public Integer getTotalCents() { return totalCents; }
    public void setTotalCents(Integer totalCents) { this.totalCents = totalCents; }
    public Integer getAmountPaid() { return amountPaid; }
    public void setAmountPaid(Integer amountPaid) { this.amountPaid = amountPaid; }
    public Integer getBalanceCents() { return balanceCents; }
    public void setBalanceCents(Integer balanceCents) { this.balanceCents = balanceCents; }
    public Integer getPointsUsed() { return pointsUsed; }
    public void setPointsUsed(Integer pointsUsed) { this.pointsUsed = pointsUsed; }
    public Integer getPointsDeductCents() { return pointsDeductCents; }
    public void setPointsDeductCents(Integer pointsDeductCents) { this.pointsDeductCents = pointsDeductCents; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public String getRemark2() { return remark2; }
    public void setRemark2(String remark2) { this.remark2 = remark2; }
    public String getRedeemCode() { return redeemCode; }
    public void setRedeemCode(String redeemCode) { this.redeemCode = redeemCode; }
    public String getPayTradeNo() { return payTradeNo; }
    public void setPayTradeNo(String payTradeNo) { this.payTradeNo = payTradeNo; }
    public Date getPaidTime() { return paidTime; }
    public void setPaidTime(Date paidTime) { this.paidTime = paidTime; }
    public Date getRedeemedTime() { return redeemedTime; }
    public void setRedeemedTime(Date redeemedTime) { this.redeemedTime = redeemedTime; }
    public String getRedeemedBy() { return redeemedBy; }
    public void setRedeemedBy(String redeemedBy) { this.redeemedBy = redeemedBy; }
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public List<FishMallOrderItem> getItems() { return items; }
    public void setItems(List<FishMallOrderItem> items) { this.items = items; }
}
