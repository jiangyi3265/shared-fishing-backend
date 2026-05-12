package com.ruoyi.fishing.domain;

import java.util.Date;
import com.ruoyi.common.core.domain.BaseEntity;

public class FishPointsExchange extends BaseEntity
{
    private Long exchangeId;
    private Long userId;
    private String nickname;
    private Long goodsId;
    private String goodsName;
    private Integer pointsCost;
    private Integer status;
    private Date createTime;
    private Date updateTime;

    public Long getExchangeId() { return exchangeId; }
    public void setExchangeId(Long exchangeId) { this.exchangeId = exchangeId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public Long getGoodsId() { return goodsId; }
    public void setGoodsId(Long goodsId) { this.goodsId = goodsId; }
    public String getGoodsName() { return goodsName; }
    public void setGoodsName(String goodsName) { this.goodsName = goodsName; }
    public Integer getPointsCost() { return pointsCost; }
    public void setPointsCost(Integer pointsCost) { this.pointsCost = pointsCost; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
    public Date getUpdateTime() { return updateTime; }
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }
}
