package com.ruoyi.fishing.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.fishing.domain.FishCatchComment;

public interface FishCatchCommentMapper
{
    List<FishCatchComment> selectByCatchId(@Param("catchId") Long catchId);
    int insert(FishCatchComment comment);
    int delete(@Param("commentId") Long commentId);
    int incrementCommentCount(@Param("catchId") Long catchId);
    int decrementCommentCount(@Param("catchId") Long catchId);
}
