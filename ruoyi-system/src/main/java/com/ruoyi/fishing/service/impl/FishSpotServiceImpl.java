package com.ruoyi.fishing.service.impl;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.fishing.domain.FishReservation;
import com.ruoyi.fishing.domain.FishSpot;
import com.ruoyi.fishing.mapper.FishReservationMapper;
import com.ruoyi.fishing.mapper.FishSpotMapper;
import com.ruoyi.fishing.service.IFishSpotService;
import com.ruoyi.fishing.service.IFishUserService;

@Service
public class FishSpotServiceImpl implements IFishSpotService
{
    @Autowired private FishSpotMapper spotMapper;
    @Autowired private FishReservationMapper resvMapper;
    @Autowired private IFishUserService userService;

    @Override public FishSpot selectById(Long spotId) { return spotMapper.selectById(spotId); }
    @Override public List<FishSpot> selectList(FishSpot query) { return spotMapper.selectList(query); }
    @Override public List<FishSpot> selectAvailableByVenue(Long venueId) { return spotMapper.selectAvailableByVenue(venueId); }

    @Override
    public int insertSpot(FishSpot spot) {
        if (spot.getVenueId() == null) throw new ServiceException("请选择所属钓场");
        spot.setCreateBy(safeUser());
        spot.setCreateTime(DateUtils.getNowDate());
        if (spot.getStatus() == null) spot.setStatus("0");
        if (spot.getSpotType() == null) spot.setSpotType("normal");
        return spotMapper.insert(spot);
    }

    @Override
    public int updateSpot(FishSpot spot) {
        spot.setUpdateBy(safeUser());
        spot.setUpdateTime(DateUtils.getNowDate());
        return spotMapper.update(spot);
    }

    @Override public int deleteByIds(Long[] spotIds) { return spotMapper.deleteByIds(spotIds); }

    // ===== 预订 =====

    @Override public FishReservation selectReservationById(Long id) { return resvMapper.selectById(id); }
    @Override public List<FishReservation> selectReservationList(FishReservation q) { return resvMapper.selectList(q); }
    @Override public List<FishReservation> selectReservationsByUser(Long userId) { return resvMapper.selectByUser(userId); }

    @Override
    @Transactional
    public FishReservation submitReservation(Long userId, Long venueId, Long spotId, String reserveDate, String timeSlot)
    {
        userService.assertNotBlacklisted(userId);

        FishSpot spot = spotMapper.selectById(spotId);
        if (spot == null || !"0".equals(spot.getStatus())) throw new ServiceException("钓位不可用");

        Date date;
        try { date = new SimpleDateFormat("yyyy-MM-dd").parse(reserveDate); }
        catch (Exception e) { throw new ServiceException("日期格式错误"); }

        FishReservation existing = resvMapper.selectBySpotAndDate(spotId, date, timeSlot == null ? "" : timeSlot);
        if (existing != null) throw new ServiceException("该钓位该时段已被预订");

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);

        FishReservation r = new FishReservation();
        r.setReservationNo("B" + System.currentTimeMillis() + String.format("%04d", ThreadLocalRandom.current().nextInt(10000)));
        r.setUserId(userId);
        r.setVenueId(venueId);
        r.setSpotId(spotId);
        r.setReserveDate(date);
        r.setTimeSlot(timeSlot == null ? "" : timeSlot);
        r.setDepositCents(spot.getExtraFeeCents() != null ? spot.getExtraFeeCents() : 0);
        r.setStatus(1);
        r.setExpireTime(cal.getTime());
        resvMapper.insert(r);
        return r;
    }

    @Override
    public int cancelReservation(Long reservationId, Long userId, String reason) {
        FishReservation r = resvMapper.selectById(reservationId);
        if (r == null) throw new ServiceException("预订不存在");
        if (userId != null && !r.getUserId().equals(userId)) throw new ServiceException("无权操作");
        if (r.getStatus() >= 2) throw new ServiceException("当前状态不可取消");
        resvMapper.updateStatus(reservationId, 3);
        return 1;
    }

    @Override
    public int confirmReservation(Long reservationId) {
        return resvMapper.updateStatus(reservationId, 1);
    }

    @Override
    public int arriveReservation(Long reservationId) {
        resvMapper.updateArriveTime(reservationId, new Date());
        return 1;
    }

    @Override
    public int expireOverdue() {
        return resvMapper.batchExpire(new Date());
    }

    private String safeUser() {
        try { return SecurityUtils.getUsername(); } catch (Exception e) { return "system"; }
    }
}
