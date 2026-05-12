package com.ruoyi.fishing.service;

import java.util.List;
import com.ruoyi.fishing.domain.FishSpot;
import com.ruoyi.fishing.domain.FishReservation;

public interface IFishSpotService
{
    FishSpot selectById(Long spotId);
    List<FishSpot> selectList(FishSpot query);
    List<FishSpot> selectAvailableByVenue(Long venueId);
    int insertSpot(FishSpot spot);
    int updateSpot(FishSpot spot);
    int deleteByIds(Long[] spotIds);

    FishReservation selectReservationById(Long reservationId);
    List<FishReservation> selectReservationList(FishReservation query);
    List<FishReservation> selectReservationsByUser(Long userId);
    FishReservation submitReservation(Long userId, Long venueId, Long spotId, String reserveDate, String timeSlot);
    int cancelReservation(Long reservationId, Long userId, String reason);
    int confirmReservation(Long reservationId);
    int arriveReservation(Long reservationId);
    int expireOverdue();
}
