package com.ruoyi.fishing.mapper;

import java.util.Date;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.fishing.domain.FishReservation;

public interface FishReservationMapper
{
    FishReservation selectById(Long reservationId);
    List<FishReservation> selectList(FishReservation query);
    List<FishReservation> selectByUser(Long userId);
    FishReservation selectBySpotAndDate(@Param("spotId") Long spotId, @Param("reserveDate") Date reserveDate, @Param("timeSlot") String timeSlot);
    int insert(FishReservation r);
    int updateStatus(@Param("reservationId") Long reservationId, @Param("status") int status);
    int updateArriveTime(@Param("reservationId") Long reservationId, @Param("arriveTime") Date arriveTime);
    List<FishReservation> selectExpired(@Param("now") Date now);
    int batchExpire(@Param("now") Date now);
}
