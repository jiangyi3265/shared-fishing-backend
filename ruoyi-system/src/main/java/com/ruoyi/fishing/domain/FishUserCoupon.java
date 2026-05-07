package com.ruoyi.fishing.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 用户优惠券 fish_user_coupon
 */
public class FishUserCoupon extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long couponId;
    private Long templateId;

    @Excel(name = "用户ID")
    private Long userId;

    @Excel(name = "名称")
    private String title;

    @Excel(name = "类型")
    private String couponType;

    private Integer couponValue;
    private Integer minAmountCents;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "过期时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date expireTime;

    private Integer used;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date usedTime;

    private Long orderId;
    private String source;

    public Long getCouponId() { return couponId; }
    public void setCouponId(Long couponId) { this.couponId = couponId; }
    public Long getTemplateId() { return templateId; }
    public void setTemplateId(Long templateId) { this.templateId = templateId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getCouponType() { return couponType; }
    public void setCouponType(String couponType) { this.couponType = couponType; }
    public Integer getCouponValue() { return couponValue; }
    public void setCouponValue(Integer couponValue) { this.couponValue = couponValue; }
    public Integer getMinAmountCents() { return minAmountCents; }
    public void setMinAmountCents(Integer minAmountCents) { this.minAmountCents = minAmountCents; }
    public Date getExpireTime() { return expireTime; }
    public void setExpireTime(Date expireTime) { this.expireTime = expireTime; }
    public Integer getUsed() { return used; }
    public void setUsed(Integer used) { this.used = used; }
    public Date getUsedTime() { return usedTime; }
    public void setUsedTime(Date usedTime) { this.usedTime = usedTime; }
    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
}
