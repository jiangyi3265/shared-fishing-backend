package com.ruoyi.fishing.domain;

import com.ruoyi.common.core.domain.BaseEntity;

public class FishPointsGoods extends BaseEntity
{
    private static final long serialVersionUID = 1L;
    private Long goodsId;
    private String name;
    private String image;
    private String type;
    private Integer pointsCost;
    private Integer stock;
    private Long relatedId;
    private Integer durationMinutes;
    private Integer sortNum;
    private String status;

    public Long getGoodsId() { return goodsId; }
    public void setGoodsId(Long goodsId) { this.goodsId = goodsId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public Integer getPointsCost() { return pointsCost; }
    public void setPointsCost(Integer pointsCost) { this.pointsCost = pointsCost; }
    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }
    public Long getRelatedId() { return relatedId; }
    public void setRelatedId(Long relatedId) { this.relatedId = relatedId; }
    public Integer getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; }
    public Integer getSortNum() { return sortNum; }
    public void setSortNum(Integer sortNum) { this.sortNum = sortNum; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
