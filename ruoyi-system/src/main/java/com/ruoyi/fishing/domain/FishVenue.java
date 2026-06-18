package com.ruoyi.fishing.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 钓场 fish_venue
 */
public class FishVenue extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long venueId;

    @Excel(name = "钓场名称")
    private String name;

    @Excel(name = "地址")
    private String address;

    private String notice;

    @Excel(name = "联系方式")
    private String phone;

    private Long ruleId;

    /** 路人鱼获单价(分/斤) */
    @Excel(name = "路人鱼获单价(分/斤)")
    private Integer fishPriceCents;

    /** 会员鱼获单价(分/斤) */
    @Excel(name = "会员鱼获单价(分/斤)")
    private Integer fishMemberPriceCents;

    @Excel(name = "状态", readConverterExp = "0=正常,1=停用")
    private String status;

    private String delFlag;

    public Long getVenueId() { return venueId; }
    public void setVenueId(Long venueId) { this.venueId = venueId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getNotice() { return notice; }
    public void setNotice(String notice) { this.notice = notice; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public Long getRuleId() { return ruleId; }
    public void setRuleId(Long ruleId) { this.ruleId = ruleId; }
    public Integer getFishPriceCents() { return fishPriceCents; }
    public void setFishPriceCents(Integer fishPriceCents) { this.fishPriceCents = fishPriceCents; }
    public Integer getFishMemberPriceCents() { return fishMemberPriceCents; }
    public void setFishMemberPriceCents(Integer fishMemberPriceCents) { this.fishMemberPriceCents = fishMemberPriceCents; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getDelFlag() { return delFlag; }
    public void setDelFlag(String delFlag) { this.delFlag = delFlag; }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("venueId", venueId).append("name", name).append("address", address)
            .append("notice", notice).append("phone", phone).append("ruleId", ruleId)
            .append("status", status).append("createTime", getCreateTime()).toString();
    }
}
