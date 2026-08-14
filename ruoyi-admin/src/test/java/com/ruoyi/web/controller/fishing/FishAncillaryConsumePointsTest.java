package com.ruoyi.web.controller.fishing;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import com.ruoyi.fishing.domain.FishCompetition;
import com.ruoyi.fishing.domain.FishRegistration;
import com.ruoyi.fishing.domain.FishRentalGoods;
import com.ruoyi.fishing.domain.FishRentalOrder;
import com.ruoyi.fishing.domain.FishUserBalance;
import com.ruoyi.fishing.mapper.FishAdMapper;
import com.ruoyi.fishing.mapper.FishCompetitionMapper;
import com.ruoyi.fishing.mapper.FishRegistrationMapper;
import com.ruoyi.fishing.mapper.FishRentalMapper;
import com.ruoyi.fishing.service.IFishBalanceService;
import com.ruoyi.fishing.service.IFishPointsService;
import com.ruoyi.fishing.service.IFishUserService;
import com.ruoyi.fishing.service.impl.FishCompetitionServiceImpl;
import com.ruoyi.fishing.service.impl.FishRegistrationServiceImpl;
import com.ruoyi.fishing.service.impl.FishRentalServiceImpl;

public class FishAncillaryConsumePointsTest
{
    @Test
    public void rentalAwardsPointsForRentButNotRefundableDeposit() throws Exception
    {
        FishRentalMapper mapper = org.mockito.Mockito.mock(FishRentalMapper.class);
        IFishBalanceService balanceService = org.mockito.Mockito.mock(IFishBalanceService.class);
        IFishPointsService pointsService = org.mockito.Mockito.mock(IFishPointsService.class);
        IFishUserService userService = org.mockito.Mockito.mock(IFishUserService.class);
        FishRentalServiceImpl service = new FishRentalServiceImpl();
        inject(service, "mapper", mapper);
        inject(service, "balanceService", balanceService);
        inject(service, "pointsService", pointsService);
        inject(service, "userService", userService);

        FishRentalGoods goods = new FishRentalGoods();
        goods.setGoodsId(8L);
        goods.setName("鱼护");
        goods.setStatus("0");
        goods.setDepositCents(10000);
        goods.setRentCents(2999);
        FishUserBalance balance = new FishUserBalance();
        balance.setBalanceCents(20000);
        when(mapper.selectGoodsById(8L)).thenReturn(goods);
        when(balanceService.getBalance(7L)).thenReturn(balance);
        when(mapper.decreaseStock(8L)).thenReturn(1);

        FishRentalOrder result = service.rent(7L, 8L);

        verify(balanceService).applyDelta(eq(7L), eq(-12999), eq("consume_fishing"),
                eq(result.getOrderNo()), any(String.class), eq("system"));
        verify(pointsService).prepareConsumeReward(7L, 2999, "rental", result.getOrderNo());
        assertTrue(result.getOrderNo().startsWith("L"));
    }

    @Test
    public void competitionEntryFeeAwardsConsumptionPoints() throws Exception
    {
        FishCompetitionMapper mapper = org.mockito.Mockito.mock(FishCompetitionMapper.class);
        IFishBalanceService balanceService = org.mockito.Mockito.mock(IFishBalanceService.class);
        IFishPointsService pointsService = org.mockito.Mockito.mock(IFishPointsService.class);
        IFishUserService userService = org.mockito.Mockito.mock(IFishUserService.class);
        FishCompetitionServiceImpl service = new FishCompetitionServiceImpl();
        inject(service, "mapper", mapper);
        inject(service, "balanceService", balanceService);
        inject(service, "pointsService", pointsService);
        inject(service, "userService", userService);

        FishCompetition competition = new FishCompetition();
        competition.setCompId(3L);
        competition.setStatus(0);
        competition.setMaxPlayers(50);
        competition.setEntryFeeCents(6800);
        when(mapper.selectById(3L)).thenReturn(competition);
        when(mapper.countEntries(3L)).thenReturn(4);

        service.enter(3L, 7L, "钓友", "13800000000");

        verify(balanceService).applyDelta(7L, -6800, "consume_fishing",
                "COMP3", "比赛报名费", "system");
        verify(pointsService).prepareConsumeReward(7L, 6800, "competition", "COMP3");
    }

    @Test
    public void paidActivityAwardsPointsAndRepeatedCallbackStaysIdempotent() throws Exception
    {
        FishRegistrationMapper mapper = org.mockito.Mockito.mock(FishRegistrationMapper.class);
        IFishPointsService pointsService = org.mockito.Mockito.mock(IFishPointsService.class);
        FishRegistrationServiceImpl service = new FishRegistrationServiceImpl();
        inject(service, "regMapper", mapper);
        inject(service, "adMapper", org.mockito.Mockito.mock(FishAdMapper.class));
        inject(service, "userService", org.mockito.Mockito.mock(IFishUserService.class));
        inject(service, "pointsService", pointsService);

        FishRegistration registration = new FishRegistration();
        registration.setRegId(12L);
        registration.setUserId(7L);
        registration.setFeeCents(2999);
        registration.setPaid(1);
        when(mapper.selectFishRegistrationByRegId(12L)).thenReturn(registration);

        service.pay(12L);

        verify(mapper, never()).updateFishRegistration(any(FishRegistration.class));
        verify(pointsService).prepareConsumeReward(7L, 2999, "activity", "ACT12");
    }

    private void inject(Object target, String fieldName, Object value) throws Exception
    {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}
