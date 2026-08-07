package com.ruoyi.web.controller.fishing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.junit.Before;
import org.junit.Test;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.fishing.domain.FishMallOrder;
import com.ruoyi.fishing.domain.FishUser;
import com.ruoyi.fishing.service.AppTokenService;
import com.ruoyi.fishing.service.IFishMallService;
import com.ruoyi.fishing.service.IFishUserService;
import com.ruoyi.fishing.service.IWxPayService;

public class AppApiControllerMallPayTest
{
    private static final Long USER_ID = 7L;
    private static final Long ORDER_ID = 21L;

    private AppApiController controller;
    private IFishMallService mallService;
    private IFishUserService userService;
    private IWxPayService wxPayService;

    @Before
    public void setUp() throws Exception
    {
        controller = new AppApiController();
        mallService = mock(IFishMallService.class);
        userService = mock(IFishUserService.class);
        wxPayService = mock(IWxPayService.class);
        AppTokenService tokenService = mock(AppTokenService.class);
        HttpServletRequest request = mock(HttpServletRequest.class);

        setField("mallService", mallService);
        setField("userService", userService);
        setField("wxPayService", wxPayService);
        setField("appTokenService", tokenService);
        setField("request", request);
        when(request.getHeader("Authorization")).thenReturn("Bearer test-token");
        when(tokenService.resolveUserId("Bearer test-token")).thenReturn(USER_ID);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void paidOrderReturnsWithoutCreatingAnotherPrepay()
    {
        FishMallOrder order = order(1);
        when(mallService.getOrder(ORDER_ID)).thenReturn(order);

        AjaxResult result = controller.payMallOrder(ORDER_ID);

        Map<String, Object> data = (Map<String, Object>) result.get(AjaxResult.DATA_TAG);
        assertSame(order, data.get("order"));
        assertFalse((Boolean) data.get("needWxPay"));
        verify(wxPayService, never()).createPrepay(anyString(), anyInt(), anyString(), anyString());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void unpaidOrderReusesOriginalOrderNumber()
    {
        FishMallOrder order = order(0);
        order.setTotalCents(6800);
        order.setPointsDeductCents(300);
        order.setBalanceCents(500);
        when(mallService.getOrder(ORDER_ID)).thenReturn(order);
        when(wxPayService.isEnabled()).thenReturn(true);
        FishUser user = new FishUser();
        user.setUserId(USER_ID);
        user.setOpenid("openid-7");
        when(userService.selectFishUserByUserId(USER_ID)).thenReturn(user);
        when(wxPayService.createPrepay(eq("M202608060001"), eq(6000), eq("openid-7"), anyString()))
                .thenReturn(Collections.<String, Object>singletonMap("prepayId", "wx-prepay"));

        AjaxResult result = controller.payMallOrder(ORDER_ID);

        Map<String, Object> data = (Map<String, Object>) result.get(AjaxResult.DATA_TAG);
        assertTrue((Boolean) data.get("needWxPay"));
        verify(wxPayService).createPrepay(eq("M202608060001"), eq(6000), eq("openid-7"), anyString());
    }

    @Test
    public void canceledOrderCannotBePaid()
    {
        when(mallService.getOrder(ORDER_ID)).thenReturn(order(3));

        AjaxResult result = controller.payMallOrder(ORDER_ID);

        assertEquals(500, result.get(AjaxResult.CODE_TAG));
        assertTrue(String.valueOf(result.get(AjaxResult.MSG_TAG)).contains("已取消"));
    }

    private FishMallOrder order(int status)
    {
        FishMallOrder order = new FishMallOrder();
        order.setMallOrderId(ORDER_ID);
        order.setMallOrderNo("M202608060001");
        order.setUserId(USER_ID);
        order.setStatus(status);
        order.setTotalCents(1000);
        order.setBalanceCents(0);
        order.setPointsDeductCents(0);
        return order;
    }

    private void setField(String name, Object value) throws Exception
    {
        Field field = AppApiController.class.getDeclaredField(name);
        field.setAccessible(true);
        field.set(controller, value);
    }
}
