package com.ruoyi.fishing.domain;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

public class FishMallCategory extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long catId;

    @Excel(name = "分类名")
    private String name;

    private String icon;
    private Integer sort;
    private String status;

    public Long getCatId() { return catId; }
    public void setCatId(Long catId) { this.catId = catId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }
    public Integer getSort() { return sort; }
    public void setSort(Integer sort) { this.sort = sort; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
