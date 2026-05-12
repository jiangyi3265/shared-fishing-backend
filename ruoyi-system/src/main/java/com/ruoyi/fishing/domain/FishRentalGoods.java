package com.ruoyi.fishing.domain;

import com.ruoyi.common.core.domain.BaseEntity;

public class FishRentalGoods extends BaseEntity
{
    private static final long serialVersionUID = 1L;
    private Long goodsId;
    private String name;
    private String image;
    private String category;
    private Integer depositCents;
    private Integer rentCents;
    private String rentUnit;
    private Integer stock;
    private String description;
    private Integer sortNum;
    private String status;
    private String delFlag;

    public Long getGoodsId() { return goodsId; }
    public void setGoodsId(Long goodsId) { this.goodsId = goodsId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public Integer getDepositCents() { return depositCents; }
    public void setDepositCents(Integer depositCents) { this.depositCents = depositCents; }
    public Integer getRentCents() { return rentCents; }
    public void setRentCents(Integer rentCents) { this.rentCents = rentCents; }
    public String getRentUnit() { return rentUnit; }
    public void setRentUnit(String rentUnit) { this.rentUnit = rentUnit; }
    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Integer getSortNum() { return sortNum; }
    public void setSortNum(Integer sortNum) { this.sortNum = sortNum; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getDelFlag() { return delFlag; }
    public void setDelFlag(String delFlag) { this.delFlag = delFlag; }
}
