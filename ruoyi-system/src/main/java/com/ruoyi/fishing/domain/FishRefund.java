package com.ruoyi.fishing.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 订单退款 fish_refund
 *
 * 状态机：0 待审核 → 1 退款中 → 2 已完成 / 4 退款失败
 *                  ↘ 3 已驳回
 */
public class FishRefund extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long refundId;

    @Excel(name = "退款单号")
    private String refundNo;

    @Excel(name = "订单ID")
    private Long orderId;

    @Excel(name = "订单号")
    private String orderNo;

    @Excel(name = "用户ID")
    private Long userId;

    /** 订单类型：fishing 钓场 / mall 商城 */
    private String orderType;

    @Excel(name = "申请退款(分)")
    private Integer applyAmountCents;

    @Excel(name = "实际退款(分)")
    private Integer refundAmountCents;

    @Excel(name = "原因")
    private String reason;

    @Excel(name = "状态", readConverterExp = "0=待审核,1=退款中,2=已完成,3=已驳回,4=退款失败")
    private Integer status;

    private String auditRemark;
    private String auditBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date auditTime;

    private String wxRefundNo;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date finishTime;

    // 联表字段
    private String nickname;
    private String venueName;

    public Long getRefundId() { return refundId; }
    public void setRefundId(Long refundId) { this.refundId = refundId; }
    public String getRefundNo() { return refundNo; }
    public void setRefundNo(String refundNo) { this.refundNo = refundNo; }
    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
    public String getOrderNo() { return orderNo; }
    public void setOrderNo(String orderNo) { this.orderNo = orderNo; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getOrderType() { return orderType; }
    public void setOrderType(String orderType) { this.orderType = orderType; }
    public Integer getApplyAmountCents() { return applyAmountCents; }
    public void setApplyAmountCents(Integer applyAmountCents) { this.applyAmountCents = applyAmountCents; }
    public Integer getRefundAmountCents() { return refundAmountCents; }
    public void setRefundAmountCents(Integer refundAmountCents) { this.refundAmountCents = refundAmountCents; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public String getAuditRemark() { return auditRemark; }
    public void setAuditRemark(String auditRemark) { this.auditRemark = auditRemark; }
    public String getAuditBy() { return auditBy; }
    public void setAuditBy(String auditBy) { this.auditBy = auditBy; }
    public Date getAuditTime() { return auditTime; }
    public void setAuditTime(Date auditTime) { this.auditTime = auditTime; }
    public String getWxRefundNo() { return wxRefundNo; }
    public void setWxRefundNo(String wxRefundNo) { this.wxRefundNo = wxRefundNo; }
    public Date getFinishTime() { return finishTime; }
    public void setFinishTime(Date finishTime) { this.finishTime = finishTime; }
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public String getVenueName() { return venueName; }
    public void setVenueName(String venueName) { this.venueName = venueName; }
}
