package com.ruoyi.fishing.domain;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

public class FishMallGoods extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long goodsId;

    @Excel(name = "分类ID")
    private Long catId;

    @Excel(name = "商品名")
    private String name;

    private String subtitle;
    private String cover;
    private String description;

    @Excel(name = "售价(分)")
    private Integer priceCents;

    @Excel(name = "库存")
    private Integer stock;

    @Excel(name = "已售")
    private Integer sales;

    private Integer sort;

    @Excel(name = "状态", readConverterExp = "0=上架,1=下架")
    private String status;

    private String catName; // 联表

    public Long getGoodsId() { return goodsId; }
    public void setGoodsId(Long goodsId) { this.goodsId = goodsId; }
    public Long getCatId() { return catId; }
    public void setCatId(Long catId) { this.catId = catId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getSubtitle() { return subtitle; }
    public void setSubtitle(String subtitle) { this.subtitle = subtitle; }
    public String getCover() { return cover; }
    public void setCover(String cover) { this.cover = cover; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Integer getPriceCents() { return priceCents; }
    public void setPriceCents(Integer priceCents) { this.priceCents = priceCents; }
    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }
    public Integer getSales() { return sales; }
    public void setSales(Integer sales) { this.sales = sales; }
    public Integer getSort() { return sort; }
    public void setSort(Integer sort) { this.sort = sort; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getCatName() { return catName; }
    public void setCatName(String catName) { this.catName = catName; }
}
