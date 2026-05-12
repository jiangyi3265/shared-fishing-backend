package com.ruoyi.fishing.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.fishing.domain.FishCompetition;
import com.ruoyi.fishing.domain.FishCompetitionEntry;

public interface FishCompetitionMapper
{
    FishCompetition selectById(Long compId);
    List<FishCompetition> selectList(FishCompetition query);
    List<FishCompetition> selectActiveList(Long venueId);
    int insert(FishCompetition comp);
    int update(FishCompetition comp);
    int updateStatus(@Param("compId") Long compId, @Param("status") int status);
    int countEntries(Long compId);

    List<FishCompetitionEntry> selectEntries(Long compId);
    List<FishCompetitionEntry> selectRanking(Long compId);
    FishCompetitionEntry selectEntry(@Param("compId") Long compId, @Param("userId") Long userId);
    int insertEntry(FishCompetitionEntry entry);
    int updateEntryWeigh(@Param("entryId") Long entryId, @Param("weightGram") int weightGram,
                         @Param("fishCount") int fishCount, @Param("weighBy") String weighBy, @Param("weighImage") String weighImage);
    int updateEntryRanking(@Param("entryId") Long entryId, @Param("ranking") int ranking, @Param("prizeCents") int prizeCents);
    int updateEntryPrizeStatus(@Param("entryId") Long entryId, @Param("prizeStatus") int prizeStatus);
}
