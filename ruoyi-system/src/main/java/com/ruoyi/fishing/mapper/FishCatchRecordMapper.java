package com.ruoyi.fishing.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.fishing.domain.FishCatchRecord;

public interface FishCatchRecordMapper
{
    FishCatchRecord selectById(Long catchId);
    List<FishCatchRecord> selectList(FishCatchRecord query);
    List<FishCatchRecord> selectPublicList();
    List<FishCatchRecord> selectByUser(Long userId);
    int insert(FishCatchRecord record);
    int update(FishCatchRecord record);
    int deleteByIds(Long[] catchIds);
    int incrementLike(Long catchId);
    int decrementLike(Long catchId);
    int insertLike(@Param("userId") Long userId, @Param("catchId") Long catchId);
    int deleteLike(@Param("userId") Long userId, @Param("catchId") Long catchId);
    int countLike(@Param("userId") Long userId, @Param("catchId") Long catchId);
}
