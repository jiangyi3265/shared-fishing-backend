package com.ruoyi.fishing.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;

public class FishRentalOrder extends BaseEntity
{
    private Long orderId;
    private String orderNo;
    private Long userId;
    private String nickname;
    private Long goodsId;
    private String goodsName;
    private Integer depositCents;
    private Integer rentCents;
    private Integer status;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date rentTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date returnTime;
    private Integer depositRefunded;
    private String remark;
    private Date createTime;

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
    public String getOrderNo() { return orderNo; }
    public void setOrderNo(String orderNo) { this.orderNo = orderNo; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public Long getGoodsId() { return goodsId; }
    public void setGoodsId(Long goodsId) { this.goodsId = goodsId; }
    public String getGoodsName() { return goodsName; }
    public void setGoodsName(String goodsName) { this.goodsName = goodsName; }
    public Integer getDepositCents() { return depositCents; }
    public void setDepositCents(Integer depositCents) { this.depositCents = depositCents; }
    public Integer getRentCents() { return rentCents; }
    public void setRentCents(Integer rentCents) { this.rentCents = rentCents; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public Date getRentTime() { return rentTime; }
    public void setRentTime(Date rentTime) { this.rentTime = rentTime; }
    public Date getReturnTime() { return returnTime; }
    public void setReturnTime(Date returnTime) { this.returnTime = returnTime; }
    public Integer getDepositRefunded() { return depositRefunded; }
    public void setDepositRefunded(Integer depositRefunded) { this.depositRefunded = depositRefunded; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
}
