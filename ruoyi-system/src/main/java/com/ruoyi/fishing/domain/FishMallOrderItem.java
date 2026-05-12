package com.ruoyi.fishing.domain;

public class FishMallOrderItem
{
    private Long itemId;
    private Long mallOrderId;
    private Long goodsId;
    private String name;
    private String subtitle;
    private String cover;
    private Integer priceCents;
    private Integer qty;

    public Long getItemId() { return itemId; }
    public void setItemId(Long itemId) { this.itemId = itemId; }
    public Long getMallOrderId() { return mallOrderId; }
    public void setMallOrderId(Long mallOrderId) { this.mallOrderId = mallOrderId; }
    public Long getGoodsId() { return goodsId; }
    public void setGoodsId(Long goodsId) { this.goodsId = goodsId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getSubtitle() { return subtitle; }
    public void setSubtitle(String subtitle) { this.subtitle = subtitle; }
    public String getCover() { return cover; }
    public void setCover(String cover) { this.cover = cover; }
    public Integer getPriceCents() { return priceCents; }
    public void setPriceCents(Integer priceCents) { this.priceCents = priceCents; }
    public Integer getQty() { return qty; }
    public void setQty(Integer qty) { this.qty = qty; }
}
