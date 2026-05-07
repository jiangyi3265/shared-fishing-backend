package com.ruoyi.fishing.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 钓场二维码 fish_qrcode
 */
public class FishQrcode extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long qrId;

    @Excel(name = "钓场ID")
    private Long venueId;

    @Excel(name = "类型")
    private String qrType;

    private String sceneValue;

    @Excel(name = "状态", readConverterExp = "0=正常,1=停用")
    private String status;

    public Long getQrId() { return qrId; }
    public void setQrId(Long qrId) { this.qrId = qrId; }
    public Long getVenueId() { return venueId; }
    public void setVenueId(Long venueId) { this.venueId = venueId; }
    public String getQrType() { return qrType; }
    public void setQrType(String qrType) { this.qrType = qrType; }
    public String getSceneValue() { return sceneValue; }
    public void setSceneValue(String sceneValue) { this.sceneValue = sceneValue; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("qrId", qrId).append("venueId", venueId).append("qrType", qrType)
            .append("sceneValue", sceneValue).append("status", status).toString();
    }
}
