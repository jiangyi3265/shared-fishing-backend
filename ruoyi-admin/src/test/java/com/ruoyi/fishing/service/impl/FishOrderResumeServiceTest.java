package com.ruoyi.fishing.service.impl;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.util.Date;
import org.junit.Before;
import org.junit.Test;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.fishing.domain.FishOrder;
import com.ruoyi.fishing.mapper.FishOrderMapper;
import com.ruoyi.fishing.mapper.FishUserCouponMapper;
import com.ruoyi.fishing.service.IWxPayService;

public class FishOrderResumeServiceTest
{
    private static final Long USER_ID = 7L;
    private static final Long ORDER_ID = 18L;
    private static final Long COUPON_ID = 23L;

    private FishOrderServiceImpl service;
    private FishOrderMapper orderMapper;
    private FishUserCouponMapper couponMapper;
    private IWxPayService wxPayService;

    @Before
    public void setUp() throws Exception
    {
        service = new FishOrderServiceImpl();
        orderMapper = mock(FishOrderMapper.class);
        couponMapper = mock(FishUserCouponMapper.class);
        wxPayService = mock(IWxPayService.class);
        setField("orderMapper", orderMapper);
        setField("couponMapper", couponMapper);
        setField("wxPayService", wxPayService);
    }

    @Test
    public void pendingOrderCanResumeFromOriginalStartTime()
    {
        Date originalStart = new Date(System.currentTimeMillis() - 7_200_000L);
        FishOrder pending = order(2);
        pending.setStartTime(originalStart);
        pending.setEndTime(new Date());
        pending.setCouponId(COUPON_ID);
        pending.setAmountCents(3600);
        pending.setAmountPaid(3600);

        FishOrder running = order(1);
        running.setStartTime(originalStart);
        when(orderMapper.selectFishOrderByOrderId(ORDER_ID)).thenReturn(pending, running);
        when(orderMapper.resumePendingOrder(ORDER_ID, USER_ID)).thenReturn(1);

        FishOrder result = service.resumeOrder(USER_ID, ORDER_ID);

        assertSame(running, result);
        assertSame(originalStart, result.getStartTime());
        verify(wxPayService).closeOrder(pending.getOrderNo());
        verify(orderMapper).resumePendingOrder(ORDER_ID, USER_ID);
        verify(couponMapper).releaseCoupon(COUPON_ID, ORDER_ID);
    }

    @Test
    public void pendingOrderWithoutPrepayResumesWithoutWechatCall()
    {
        FishOrder pending = order(2);
        pending.setAmountPaid(0);
        FishOrder running = order(1);
        when(orderMapper.selectFishOrderByOrderId(ORDER_ID)).thenReturn(pending, running);
        when(orderMapper.resumePendingOrder(ORDER_ID, USER_ID)).thenReturn(1);

        assertSame(running, service.resumeOrder(USER_ID, ORDER_ID));

        verify(wxPayService, never()).closeOrder(pending.getOrderNo());
    }

    @Test
    public void paidOrderCannotResume()
    {
        when(orderMapper.selectFishOrderByOrderId(ORDER_ID)).thenReturn(order(3));

        ServiceException error = assertServiceException(() -> service.resumeOrder(USER_ID, ORDER_ID));

        assertTrue(error.getMessage().contains("已经支付"));
        verify(wxPayService, never()).closeOrder("FD-18");
        verify(orderMapper, never()).resumePendingOrder(ORDER_ID, USER_ID);
    }

    @Test
    public void stalePaymentCallbackCannotPayResumedOrder()
    {
        when(orderMapper.selectFishOrderByOrderNo("FD-18")).thenReturn(order(1));

        ServiceException error = assertServiceException(() -> service.markPaid("FD-18", "WX-18", 3600));

        assertTrue(error.getMessage().contains("恢复计时"));
        verify(orderMapper, never()).updateOrderStatusWithGuard(ORDER_ID, 1, 3);
    }

    @Test
    public void paymentCallbackAmountMustMatchPreparedAmount()
    {
        FishOrder pending = order(2);
        pending.setAmountPaid(3600);
        when(orderMapper.selectFishOrderByOrderNo("FD-18")).thenReturn(pending);

        ServiceException error = assertServiceException(() -> service.markPaid("FD-18", "WX-18", 1800));

        assertTrue(error.getMessage().contains("金额"));
        verify(orderMapper, never()).updateOrderStatusWithGuard(ORDER_ID, 2, 3);
    }

    private FishOrder order(int status)
    {
        FishOrder order = new FishOrder();
        order.setOrderId(ORDER_ID);
        order.setOrderNo("FD-18");
        order.setUserId(USER_ID);
        order.setStatus(status);
        return order;
    }

    private ServiceException assertServiceException(Runnable invocation)
    {
        try
        {
            invocation.run();
            fail("Expected ServiceException");
            return null;
        }
        catch (ServiceException error)
        {
            return error;
        }
    }

    private void setField(String name, Object value) throws Exception
    {
        Field field = FishOrderServiceImpl.class.getDeclaredField(name);
        field.setAccessible(true);
        field.set(service, value);
    }
}
