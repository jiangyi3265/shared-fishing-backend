package com.ruoyi.fishing.domain;

import java.util.Date;

public class FishGroupMember
{
    private Long id;
    private Long groupId;
    private Long userId;
    private String nickname;
    private String avatar;
    private String role;
    private Integer status;
    private Date joinTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getGroupId() { return groupId; }
    public void setGroupId(Long groupId) { this.groupId = groupId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public Date getJoinTime() { return joinTime; }
    public void setJoinTime(Date joinTime) { this.joinTime = joinTime; }
}
