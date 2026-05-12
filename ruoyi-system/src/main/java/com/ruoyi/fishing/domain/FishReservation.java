package com.ruoyi.fishing.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

public class FishReservation extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long reservationId;

    @Excel(name = "预订号")
    private String reservationNo;

    private Long userId;
    private String nickname;
    private Long venueId;
    private Long spotId;
    private String spotName;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "预订日期", dateFormat = "yyyy-MM-dd")
    private Date reserveDate;

    private String timeSlot;

    @Excel(name = "押金(分)")
    private Integer depositCents;

    @Excel(name = "状态", readConverterExp = "0=待确认,1=已确认,2=已到场,3=已取消,4=超时释放")
    private Integer status;

    private String cancelReason;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date arriveTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date expireTime;

    private String payTradeNo;
    private Integer refundStatus;

    public Long getReservationId() { return reservationId; }
    public void setReservationId(Long reservationId) { this.reservationId = reservationId; }
    public String getReservationNo() { return reservationNo; }
    public void setReservationNo(String reservationNo) { this.reservationNo = reservationNo; }
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
    public Date getReserveDate() { return reserveDate; }
    public void setReserveDate(Date reserveDate) { this.reserveDate = reserveDate; }
    public String getTimeSlot() { return timeSlot; }
    public void setTimeSlot(String timeSlot) { this.timeSlot = timeSlot; }
    public Integer getDepositCents() { return depositCents; }
    public void setDepositCents(Integer depositCents) { this.depositCents = depositCents; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public String getCancelReason() { return cancelReason; }
    public void setCancelReason(String cancelReason) { this.cancelReason = cancelReason; }
    public Date getArriveTime() { return arriveTime; }
    public void setArriveTime(Date arriveTime) { this.arriveTime = arriveTime; }
    public Date getExpireTime() { return expireTime; }
    public void setExpireTime(Date expireTime) { this.expireTime = expireTime; }
    public String getPayTradeNo() { return payTradeNo; }
    public void setPayTradeNo(String payTradeNo) { this.payTradeNo = payTradeNo; }
    public Integer getRefundStatus() { return refundStatus; }
    public void setRefundStatus(Integer refundStatus) { this.refundStatus = refundStatus; }
}
