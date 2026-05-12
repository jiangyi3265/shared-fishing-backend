package com.ruoyi.fishing.service;

import java.util.List;
import com.ruoyi.fishing.domain.FishCatchRecord;
import com.ruoyi.fishing.domain.FishCatchComment;

public interface IFishCatchService
{
    FishCatchRecord selectById(Long catchId);
    List<FishCatchRecord> selectList(FishCatchRecord query);
    List<FishCatchRecord> selectPublicList(Long currentUserId);
    List<FishCatchRecord> selectByUser(Long userId);
    int publish(FishCatchRecord record);
    int audit(Long catchId, int status, String rejectReason);
    int setFeatured(Long catchId, boolean featured);
    int deleteByIds(Long[] catchIds);
    int toggleLike(Long userId, Long catchId);

    // 评论
    List<FishCatchComment> getComments(Long catchId);
    FishCatchComment addComment(Long catchId, Long userId, String content, Long replyToId, Long replyToUser);
    int deleteComment(Long commentId, Long catchId);
}
