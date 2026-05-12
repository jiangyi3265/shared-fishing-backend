package com.ruoyi.fishing.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.fishing.domain.FishCatchRecord;
import com.ruoyi.fishing.domain.FishCatchComment;
import com.ruoyi.fishing.mapper.FishCatchRecordMapper;
import com.ruoyi.fishing.mapper.FishCatchCommentMapper;
import com.ruoyi.fishing.service.IFishCatchService;

@Service
public class FishCatchServiceImpl implements IFishCatchService
{
    @Autowired
    private FishCatchRecordMapper mapper;

    @Autowired
    private FishCatchCommentMapper commentMapper;

    @Override public FishCatchRecord selectById(Long catchId) { return mapper.selectById(catchId); }
    @Override public List<FishCatchRecord> selectList(FishCatchRecord query) { return mapper.selectList(query); }
    @Override public List<FishCatchRecord> selectByUser(Long userId) { return mapper.selectByUser(userId); }

    @Override
    public List<FishCatchRecord> selectPublicList(Long currentUserId) {
        List<FishCatchRecord> list = mapper.selectPublicList();
        if (currentUserId != null) {
            for (FishCatchRecord r : list) {
                r.setLiked(mapper.countLike(currentUserId, r.getCatchId()) > 0);
            }
        }
        return list;
    }

    @Override
    public int publish(FishCatchRecord record) {
        record.setStatus(0);
        return mapper.insert(record);
    }

    @Override
    public int audit(Long catchId, int status, String rejectReason) {
        FishCatchRecord u = new FishCatchRecord();
        u.setCatchId(catchId);
        u.setStatus(status);
        u.setRejectReason(rejectReason);
        return mapper.update(u);
    }

    @Override
    public int setFeatured(Long catchId, boolean featured) {
        FishCatchRecord u = new FishCatchRecord();
        u.setCatchId(catchId);
        u.setIsFeatured(featured ? 1 : 0);
        return mapper.update(u);
    }

    @Override public int deleteByIds(Long[] catchIds) { return mapper.deleteByIds(catchIds); }

    @Override
    @Transactional
    public int toggleLike(Long userId, Long catchId) {
        int exists = mapper.countLike(userId, catchId);
        if (exists > 0) {
            mapper.deleteLike(userId, catchId);
            mapper.decrementLike(catchId);
            return 0;
        } else {
            mapper.insertLike(userId, catchId);
            mapper.incrementLike(catchId);
            return 1;
        }
    }

    // ===== 评论 =====

    @Override
    public List<FishCatchComment> getComments(Long catchId) {
        return commentMapper.selectByCatchId(catchId);
    }

    @Override
    @Transactional
    public FishCatchComment addComment(Long catchId, Long userId, String content, Long replyToId, Long replyToUser) {
        FishCatchComment c = new FishCatchComment();
        c.setCatchId(catchId);
        c.setUserId(userId);
        c.setContent(content);
        c.setReplyToId(replyToId);
        c.setReplyToUser(replyToUser);
        commentMapper.insert(c);
        commentMapper.incrementCommentCount(catchId);
        return c;
    }

    @Override
    @Transactional
    public int deleteComment(Long commentId, Long catchId) {
        commentMapper.delete(commentId);
        commentMapper.decrementCommentCount(catchId);
        return 1;
    }
}
