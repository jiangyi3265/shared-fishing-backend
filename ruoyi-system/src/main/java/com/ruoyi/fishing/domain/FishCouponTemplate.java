package com.ruoyi.fishing.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 优惠券模板 fish_coupon_template
 */
public class FishCouponTemplate extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long templateId;

    @Excel(name = "名称")
    private String title;

    @Excel(name = "类型")
    private String couponType;

    @Excel(name = "值")
    private Integer couponValue;

    private Integer minAmountCents;
    private Integer validDays;
    private Integer totalStock;
    private Integer issuedCount;
    private String source;

    @Excel(name = "状态", readConverterExp = "0=正常,1=停用")
    private String status;

    private String delFlag;

    public Long getTemplateId() { return templateId; }
    public void setTemplateId(Long templateId) { this.templateId = templateId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getCouponType() { return couponType; }
    public void setCouponType(String couponType) { this.couponType = couponType; }
    public Integer getCouponValue() { return couponValue; }
    public void setCouponValue(Integer couponValue) { this.couponValue = couponValue; }
    public Integer getMinAmountCents() { return minAmountCents; }
    public void setMinAmountCents(Integer minAmountCents) { this.minAmountCents = minAmountCents; }
    public Integer getValidDays() { return validDays; }
    public void setValidDays(Integer validDays) { this.validDays = validDays; }
    public Integer getTotalStock() { return totalStock; }
    public void setTotalStock(Integer totalStock) { this.totalStock = totalStock; }
    public Integer getIssuedCount() { return issuedCount; }
    public void setIssuedCount(Integer issuedCount) { this.issuedCount = issuedCount; }
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getDelFlag() { return delFlag; }
    public void setDelFlag(String delFlag) { this.delFlag = delFlag; }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("templateId", templateId).append("title", title)
            .append("couponType", couponType).append("couponValue", couponValue).toString();
    }
}
