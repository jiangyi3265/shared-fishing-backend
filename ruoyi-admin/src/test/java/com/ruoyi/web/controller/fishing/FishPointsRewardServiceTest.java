package com.ruoyi.web.controller.fishing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.util.Map;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import com.ruoyi.fishing.domain.FishPointsReward;
import com.ruoyi.fishing.mapper.FishPointsMapper;
import com.ruoyi.fishing.service.impl.FishPointsServiceImpl;

public class FishPointsRewardServiceTest
{
    @Test
    public void fullYuanEarnsFivePointsAutomaticallyAndCentsAreFloored() throws Exception
    {
        FishPointsMapper mapper = org.mockito.Mockito.mock(FishPointsMapper.class);
        FishPointsServiceImpl service = service(mapper);
        FishPointsReward pending = reward(1L, 7L, "F1001", 299, 10, 0);
        FishPointsReward credited = reward(1L, 7L, "F1001", 299, 10, 1);
        when(mapper.selectRewardBySource(7L, "F1001")).thenReturn(pending, credited);
        when(mapper.claimReward(1L)).thenReturn(1);
        when(mapper.addPoints(7L, 10)).thenReturn(1);
        when(mapper.selectUserPoints(7L)).thenReturn(110);

        FishPointsReward result = service.prepareConsumeReward(7L, 299, "fishing", "F1001");

        ArgumentCaptor<FishPointsReward> captor = ArgumentCaptor.forClass(FishPointsReward.class);
        verify(mapper).insertConsumeReward(captor.capture());
        assertEquals(Integer.valueOf(10), captor.getValue().getPoints());
        assertEquals(Integer.valueOf(299), captor.getValue().getAmountCents());
        assertSame(credited, result);
        verify(mapper).claimReward(1L);
        verify(mapper).addPoints(7L, 10);
        verify(mapper).insertPointsLog(eq(7L), eq(10), eq(110), eq("consume"), eq("F1001"), any(String.class));
    }

    @Test
    public void lessThanOneYuanDoesNotCreateReward() throws Exception
    {
        FishPointsMapper mapper = org.mockito.Mockito.mock(FishPointsMapper.class);
        FishPointsServiceImpl service = service(mapper);

        assertNull(service.prepareConsumeReward(7L, 99, "fishing", "F1002"));

        verify(mapper, never()).insertConsumeReward(any(FishPointsReward.class));
    }

    @Test
    public void firstClaimAddsPointsOnce() throws Exception
    {
        FishPointsMapper mapper = org.mockito.Mockito.mock(FishPointsMapper.class);
        FishPointsServiceImpl service = service(mapper);
        FishPointsReward pending = reward(2L, 7L, "F1003", 100, 5, 0);
        FishPointsReward claimed = reward(2L, 7L, "F1003", 100, 5, 1);
        when(mapper.selectRewardBySource(7L, "F1003")).thenReturn(pending, claimed);
        when(mapper.claimReward(2L)).thenReturn(1);
        when(mapper.addPoints(7L, 5)).thenReturn(1);
        when(mapper.selectUserPoints(7L)).thenReturn(105);

        Map<String, Object> result = service.claimConsumeReward(7L, "F1003");

        assertEquals(Boolean.TRUE, result.get("claimedNow"));
        assertEquals(105, result.get("totalPoints"));
        verify(mapper).claimReward(2L);
        verify(mapper).addPoints(7L, 5);
        verify(mapper).insertPointsLog(eq(7L), eq(5), eq(105), eq("consume"), eq("F1003"), any(String.class));
    }

    @Test
    public void repeatedClaimCannotAddPointsAgain() throws Exception
    {
        FishPointsMapper mapper = org.mockito.Mockito.mock(FishPointsMapper.class);
        FishPointsServiceImpl service = service(mapper);
        FishPointsReward claimed = reward(3L, 7L, "F1004", 100, 5, 1);
        when(mapper.selectRewardBySource(7L, "F1004")).thenReturn(claimed);
        when(mapper.selectUserPoints(7L)).thenReturn(105);

        Map<String, Object> result = service.claimConsumeReward(7L, "F1004");

        assertFalse((Boolean) result.get("claimedNow"));
        verify(mapper, never()).claimReward(3L);
        verify(mapper, never()).addPoints(eq(7L), any(Integer.class));
        verify(mapper, never()).insertPointsLog(any(Long.class), any(Integer.class), any(Integer.class),
                any(String.class), any(String.class), any(String.class));
    }

    @Test
    public void duplicatePaymentPreparationCannotAddPointsAgain() throws Exception
    {
        FishPointsMapper mapper = org.mockito.Mockito.mock(FishPointsMapper.class);
        FishPointsServiceImpl service = service(mapper);
        FishPointsReward credited = reward(4L, 7L, "F1005", 100, 5, 1);
        when(mapper.selectRewardBySource(7L, "F1005")).thenReturn(credited, credited);

        FishPointsReward result = service.prepareConsumeReward(7L, 100, "fishing", "F1005");

        assertSame(credited, result);
        verify(mapper).insertConsumeReward(any(FishPointsReward.class));
        verify(mapper, never()).claimReward(4L);
        verify(mapper, never()).addPoints(eq(7L), any(Integer.class));
    }

    private FishPointsServiceImpl service(FishPointsMapper mapper) throws Exception
    {
        FishPointsServiceImpl service = new FishPointsServiceImpl();
        Field field = FishPointsServiceImpl.class.getDeclaredField("mapper");
        field.setAccessible(true);
        field.set(service, mapper);
        return service;
    }

    private FishPointsReward reward(Long rewardId, Long userId, String sourceNo,
                                    int amountCents, int points, int status)
    {
        FishPointsReward reward = new FishPointsReward();
        reward.setRewardId(rewardId);
        reward.setUserId(userId);
        reward.setSourceType("fishing");
        reward.setSourceNo(sourceNo);
        reward.setAmountCents(amountCents);
        reward.setPoints(points);
        reward.setStatus(status);
        return reward;
    }
}
