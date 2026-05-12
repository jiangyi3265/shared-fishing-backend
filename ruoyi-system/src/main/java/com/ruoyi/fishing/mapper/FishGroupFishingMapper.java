package com.ruoyi.fishing.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.fishing.domain.FishGroupFishing;
import com.ruoyi.fishing.domain.FishGroupMember;

public interface FishGroupFishingMapper
{
    FishGroupFishing selectById(Long groupId);
    List<FishGroupFishing> selectList(FishGroupFishing query);
    List<FishGroupFishing> selectActiveList(Long venueId);
    List<FishGroupFishing> selectByUser(Long userId);
    int insert(FishGroupFishing g);
    int updateStatus(@Param("groupId") Long groupId, @Param("status") int status);
    int incrementCount(Long groupId);
    int decrementCount(Long groupId);

    List<FishGroupMember> selectMembers(Long groupId);
    FishGroupMember selectMember(@Param("groupId") Long groupId, @Param("userId") Long userId);
    int insertMember(FishGroupMember m);
    int updateMemberStatus(@Param("groupId") Long groupId, @Param("userId") Long userId, @Param("status") int status);
}
