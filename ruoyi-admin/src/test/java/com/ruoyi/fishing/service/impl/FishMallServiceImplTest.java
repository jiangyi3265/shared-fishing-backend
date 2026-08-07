package com.ruoyi.fishing.service.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import com.ruoyi.fishing.domain.FishBalanceLog;
import com.ruoyi.fishing.domain.FishMallGoods;
import com.ruoyi.fishing.domain.FishMallOrder;
import com.ruoyi.fishing.domain.FishMallOrderItem;
import com.ruoyi.fishing.domain.FishUserBalance;
import com.ruoyi.fishing.mapper.FishMallCategoryMapper;
import com.ruoyi.fishing.mapper.FishMallGoodsMapper;
import com.ruoyi.fishing.mapper.FishMallOrderMapper;
import com.ruoyi.fishing.service.IFishBalanceService;
import com.ruoyi.fishing.service.IFishPointsService;
import com.ruoyi.fishing.service.IFishUserService;

public class FishMallServiceImplTest
{
    private static final Long USER_ID = 9L;
    private static final Long ORDER_ID = 31L;
    private static final Long GOODS_ID = 41L;

    private FishMallServiceImpl service;
    private FishMallGoodsMapper goodsMapper;
    private FishMallOrderMapper orderMapper;
    private IFishBalanceService balanceService;
    private IFishPointsService pointsService;

    @Before
    public void setUp() throws Exception
    {
        service = new FishMallServiceImpl();
        goodsMapper = mock(FishMallGoodsMapper.class);
        orderMapper = mock(FishMallOrderMapper.class);
        balanceService = mock(IFishBalanceService.class);
        pointsService = mock(IFishPointsService.class);

        setField("catMapper", mock(FishMallCategoryMapper.class));
        setField("goodsMapper", goodsMapper);
        setField("orderMapper", orderMapper);
        setField("balanceService", balanceService);
        setField("userService", mock(IFishUserService.class));
        setField("pointsService", pointsService);
    }

    @Test
    public void submitOrderReservesFundsWithoutIncreasingSales()
    {
        FishMallGoods goods = goods(2000);
        FishUserBalance balance = new FishUserBalance();
        balance.setBalanceCents(1000);
        when(goodsMapper.selectById(GOODS_ID)).thenReturn(goods);
        when(goodsMapper.decreaseStock(GOODS_ID, 1)).thenReturn(1);
        when(pointsService.getUserPoints(USER_ID)).thenReturn(500);
        when(balanceService.getBalance(USER_ID)).thenReturn(balance);
        when(orderMapper.insert(any(FishMallOrder.class))).thenAnswer(invocation -> {
            FishMallOrder order = invocation.getArgument(0);
            order.setMallOrderId(ORDER_ID);
            return 1;
        });

        Map<String, Object> item = new HashMap<>();
        item.put("goodsId", GOODS_ID);
        item.put("qty", 1);
        FishMallOrder order = service.submitOrder(USER_ID, Collections.singletonList(item), "", 1L, true, 200);

        assertEquals(Integer.valueOf(1), order.getFundsReserved());
        assertEquals(Integer.valueOf(200), order.getPointsUsed());
        assertEquals(Integer.valueOf(1000), order.getBalanceCents());
        verify(pointsService).addPoints(eq(USER_ID), eq(-200), eq("mall_reserve"), anyString(), anyString());
        verify(balanceService).applyDelta(eq(USER_ID), eq(-1000), eq(FishBalanceLog.TYPE_CONSUME_MALL),
                anyString(), anyString(), eq("system"));
        verify(goodsMapper, never()).increaseSales(any(Long.class), any(Integer.class));
    }

    @Test
    public void markPaidReservedOrderDoesNotDeductFundsTwice()
    {
        FishMallOrder order = pendingOrder();
        order.setTotalCents(2000);
        order.setPointsUsed(200);
        order.setPointsDeductCents(200);
        order.setBalanceCents(1000);
        FishMallOrderItem item = new FishMallOrderItem();
        item.setGoodsId(GOODS_ID);
        item.setQty(1);
        when(orderMapper.selectByOrderNo("M-31")).thenReturn(order);
        when(orderMapper.updateStatusWithGuard(ORDER_ID, 0, 1)).thenReturn(1);
        when(orderMapper.selectItemsByOrderId(ORDER_ID)).thenReturn(Collections.singletonList(item));

        FishMallOrder paid = service.markPaid("M-31", "WX-31");

        assertEquals(Integer.valueOf(1), paid.getStatus());
        assertEquals(Integer.valueOf(800), paid.getAmountPaid());
        verify(pointsService, never()).addPoints(eq(USER_ID), eq(-200), anyString(), anyString(), anyString());
        verify(balanceService, never()).applyDelta(eq(USER_ID), eq(-1000), anyString(), anyString(), anyString(), anyString());
        verify(goodsMapper).increaseSales(GOODS_ID, 1);
    }

    @Test
    public void cancelReservedOrderReleasesFundsAndStock()
    {
        FishMallOrder order = pendingOrder();
        order.setPointsUsed(200);
        order.setBalanceCents(1000);
        FishMallOrderItem item = new FishMallOrderItem();
        item.setGoodsId(GOODS_ID);
        item.setQty(2);
        when(orderMapper.selectById(ORDER_ID)).thenReturn(order);
        when(orderMapper.updateStatusWithGuard(ORDER_ID, 0, 3)).thenReturn(1);
        when(orderMapper.selectItemsByOrderId(ORDER_ID)).thenReturn(Collections.singletonList(item));

        FishMallOrder canceled = service.cancel(ORDER_ID);

        assertEquals(Integer.valueOf(3), canceled.getStatus());
        assertEquals(Integer.valueOf(0), canceled.getFundsReserved());
        verify(pointsService).addPoints(eq(USER_ID), eq(200), eq("mall_release"), eq("M-31"), anyString());
        verify(balanceService).applyDelta(eq(USER_ID), eq(1000), eq(FishBalanceLog.TYPE_REFUND),
                eq("M-31"), anyString(), eq("system"));
        verify(goodsMapper).increaseStock(GOODS_ID, 2);
    }

    private FishMallGoods goods(int priceCents)
    {
        FishMallGoods goods = new FishMallGoods();
        goods.setGoodsId(GOODS_ID);
        goods.setName("bait");
        goods.setStatus("0");
        goods.setPriceCents(priceCents);
        goods.setStock(20);
        return goods;
    }

    private FishMallOrder pendingOrder()
    {
        FishMallOrder order = new FishMallOrder();
        order.setMallOrderId(ORDER_ID);
        order.setMallOrderNo("M-31");
        order.setUserId(USER_ID);
        order.setStatus(0);
        order.setFundsReserved(1);
        return order;
    }

    private void setField(String name, Object value) throws Exception
    {
        Field field = FishMallServiceImpl.class.getDeclaredField(name);
        field.setAccessible(true);
        field.set(service, value);
    }
}
