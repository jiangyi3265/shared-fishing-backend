package com.ruoyi.fishing.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import org.junit.Before;
import org.junit.Test;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.fishing.domain.FishWeighOrder;
import com.ruoyi.fishing.mapper.FishVenueMapper;
import com.ruoyi.fishing.mapper.FishWeighOrderMapper;
import com.ruoyi.fishing.service.IFishBalanceService;
import com.ruoyi.fishing.service.IFishPointsService;

public class FishWeighServiceImplTest
{
    private static final Long WEIGH_ID = 18L;
    private static final Long USER_ID = 7L;
    private static final String WEIGH_NO = "W202608230018";

    private FishWeighServiceImpl service;
    private FishWeighOrderMapper weighMapper;
    private IFishPointsService pointsService;

    @Before
    public void setUp() throws Exception
    {
        service = new FishWeighServiceImpl();
        weighMapper = mock(FishWeighOrderMapper.class);
        pointsService = mock(IFishPointsService.class);
        setField("weighMapper", weighMapper);
        setField("venueMapper", mock(FishVenueMapper.class));
        setField("balanceService", mock(IFishBalanceService.class));
        setField("pointsService", pointsService);
    }

    @Test
    public void markPaidStoresPaymentAndAwardsPoints()
    {
        FishWeighOrder order = pendingOrder(3680);
        when(weighMapper.selectByWeighNo(WEIGH_NO)).thenReturn(order);
        when(weighMapper.updateStatusWithGuard(WEIGH_ID, 0, 1)).thenReturn(1);

        FishWeighOrder paid = service.markPaid(WEIGH_NO, "WX-TRADE-18", 3680);

        assertEquals(Integer.valueOf(1), paid.getStatus());
        assertEquals("WX-TRADE-18", paid.getPayTradeNo());
        assertNotNull(paid.getPaidTime());
        verify(weighMapper).update(order);
        verify(pointsService).prepareConsumeReward(USER_ID, 3680, "weigh", WEIGH_NO);
    }

    @Test
    public void markPaidRejectsMismatchedCallbackAmount()
    {
        FishWeighOrder order = pendingOrder(3680);
        when(weighMapper.selectByWeighNo(WEIGH_NO)).thenReturn(order);

        try
        {
            service.markPaid(WEIGH_NO, "WX-TRADE-18", 3600);
            fail("应拒绝金额不一致的微信支付回调");
        }
        catch (ServiceException e)
        {
            assertTrue(e.getMessage().contains("支付金额"));
        }

        verify(weighMapper, never()).updateStatusWithGuard(any(Long.class), any(Integer.class), any(Integer.class));
        verify(weighMapper, never()).update(any(FishWeighOrder.class));
        verify(pointsService, never()).prepareConsumeReward(any(Long.class), any(Integer.class), any(String.class), any(String.class));
    }

    private FishWeighOrder pendingOrder(int amountCents)
    {
        FishWeighOrder order = new FishWeighOrder();
        order.setFishWeighId(WEIGH_ID);
        order.setWeighNo(WEIGH_NO);
        order.setUserId(USER_ID);
        order.setAmountCents(amountCents);
        order.setStatus(0);
        return order;
    }

    private void setField(String name, Object value) throws Exception
    {
        Field field = FishWeighServiceImpl.class.getDeclaredField(name);
        field.setAccessible(true);
        field.set(service, value);
    }
}
