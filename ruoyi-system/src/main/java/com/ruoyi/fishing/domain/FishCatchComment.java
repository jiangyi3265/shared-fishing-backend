package com.ruoyi.fishing.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

public class FishCatchComment
{
    private Long commentId;
    private Long catchId;
    private Long userId;
    private String nickname;
    private String avatar;
    private Long replyToId;
    private Long replyToUser;
    private String replyToNickname;
    private String content;
    private Integer status;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    public Long getCommentId() { return commentId; }
    public void setCommentId(Long commentId) { this.commentId = commentId; }
    public Long getCatchId() { return catchId; }
    public void setCatchId(Long catchId) { this.catchId = catchId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }
    public Long getReplyToId() { return replyToId; }
    public void setReplyToId(Long replyToId) { this.replyToId = replyToId; }
    public Long getReplyToUser() { return replyToUser; }
    public void setReplyToUser(Long replyToUser) { this.replyToUser = replyToUser; }
    public String getReplyToNickname() { return replyToNickname; }
    public void setReplyToNickname(String replyToNickname) { this.replyToNickname = replyToNickname; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
}
