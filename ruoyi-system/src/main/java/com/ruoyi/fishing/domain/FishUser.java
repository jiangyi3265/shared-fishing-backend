package com.ruoyi.fishing.domain;

import java.util.Date;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 小程序用户 fish_user
 */
public class FishUser extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long userId;

    @Excel(name = "openid")
    private String openid;

    private String unionid;

    @Excel(name = "昵称")
    private String nickname;

    private String avatar;

    @Excel(name = "手机号")
    private String phone;

    @Excel(name = "状态", readConverterExp = "0=正常,1=禁用")
    private String status;

    @Excel(name = "店员", readConverterExp = "0=普通,1=店员")
    private Integer isStaff;

    @Excel(name = "黑名单", readConverterExp = "0=正常,1=黑名单")
    private Integer isBlacklist;

    private String blacklistReason;

    private Long memberLevelId;
    private String memberLevelName;

    private Integer points;

    private Date lastLoginTime;

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getOpenid() { return openid; }
    public void setOpenid(String openid) { this.openid = openid; }
    public String getUnionid() { return unionid; }
    public void setUnionid(String unionid) { this.unionid = unionid; }
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Integer getIsStaff() { return isStaff; }
    public void setIsStaff(Integer isStaff) { this.isStaff = isStaff; }
    public Integer getIsBlacklist() { return isBlacklist; }
    public void setIsBlacklist(Integer isBlacklist) { this.isBlacklist = isBlacklist; }
    public String getBlacklistReason() { return blacklistReason; }
    public void setBlacklistReason(String blacklistReason) { this.blacklistReason = blacklistReason; }
    public Long getMemberLevelId() { return memberLevelId; }
    public void setMemberLevelId(Long memberLevelId) { this.memberLevelId = memberLevelId; }
    public String getMemberLevelName() { return memberLevelName; }
    public void setMemberLevelName(String memberLevelName) { this.memberLevelName = memberLevelName; }
    public Integer getPoints() { return points; }
    public void setPoints(Integer points) { this.points = points; }
    public Date getLastLoginTime() { return lastLoginTime; }
    public void setLastLoginTime(Date lastLoginTime) { this.lastLoginTime = lastLoginTime; }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("userId", userId).append("openid", openid).append("nickname", nickname)
            .append("phone", phone).append("status", status).toString();
    }
}
