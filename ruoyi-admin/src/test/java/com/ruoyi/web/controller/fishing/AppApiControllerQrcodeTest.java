package com.ruoyi.web.controller.fishing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.junit.Before;
import org.junit.Test;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.fishing.domain.FishOrder;
import com.ruoyi.fishing.domain.FishQrcode;
import com.ruoyi.fishing.domain.FishVenue;
import com.ruoyi.fishing.mapper.FishQrcodeMapper;
import com.ruoyi.fishing.service.AppTokenService;
import com.ruoyi.fishing.service.IFishOrderService;
import com.ruoyi.fishing.service.IFishVenueService;

public class AppApiControllerQrcodeTest
{
    private static final Long USER_ID = 7L;
    private static final Long VENUE_ID = 11L;

    private AppApiController controller;
    private IFishOrderService orderService;
    private IFishVenueService venueService;
    private FishQrcodeMapper qrcodeMapper;

    @Before
    public void setUp() throws Exception
    {
        controller = new AppApiController();
        orderService = mock(IFishOrderService.class);
        venueService = mock(IFishVenueService.class);
        qrcodeMapper = mock(FishQrcodeMapper.class);
        AppTokenService tokenService = mock(AppTokenService.class);
        HttpServletRequest request = mock(HttpServletRequest.class);

        setField("orderService", orderService);
        setField("venueService", venueService);
        setField("qrcodeMapper", qrcodeMapper);
        setField("appTokenService", tokenService);
        setField("request", request);

        when(request.getHeader("Authorization")).thenReturn("Bearer test-token");
        when(tokenService.resolveUserId("Bearer test-token")).thenReturn(USER_ID);
    }

    @Test
    public void startWithoutScanUsesActiveDefaultVenue()
    {
        FishVenue venue = venue(VENUE_ID);
        when(venueService.selectFishVenueList(any(FishVenue.class)))
                .thenReturn(Collections.singletonList(venue));
        FishOrder started = runningOrder(VENUE_ID);
        when(orderService.startOrder(USER_ID, VENUE_ID)).thenReturn(started);

        AjaxResult result = controller.startOrder(Collections.<String, Object>emptyMap());

        assertSame(started, result.get(AjaxResult.DATA_TAG));
        verify(orderService).startOrder(USER_ID, VENUE_ID);
        verify(qrcodeMapper, never()).selectFishQrcodeByQrId(any(Long.class));
    }

    @Test
    public void finishWithoutScanUsesCurrentRunningOrder()
    {
        FishOrder running = runningOrder(VENUE_ID);
        FishOrder finished = runningOrder(VENUE_ID);
        finished.setStatus(2);
        when(orderService.selectRunningOrder(USER_ID)).thenReturn(running);
        when(orderService.finishOrder(USER_ID)).thenReturn(finished);

        AjaxResult result = controller.finishOrder(null);

        assertSame(finished, result.get(AjaxResult.DATA_TAG));
        verify(orderService).finishOrder(USER_ID);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void commonQrResolvesToEndForRunningUser()
    {
        FishQrcode qr = qrcode(31L, VENUE_ID, FishQrcode.TYPE_COMMON);
        when(qrcodeMapper.selectFishQrcodeByQrId(31L)).thenReturn(qr);
        when(orderService.selectRunningOrder(USER_ID)).thenReturn(runningOrder(VENUE_ID));

        AjaxResult result = controller.resolveQrcode(31L, null);

        Map<String, Object> data = (Map<String, Object>) result.get(AjaxResult.DATA_TAG);
        assertEquals("end", data.get("action"));
        assertEquals(VENUE_ID, data.get("venueId"));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void commonQrResolvesToStartWhenNoOrderIsRunning()
    {
        FishQrcode qr = qrcode(32L, VENUE_ID, FishQrcode.TYPE_COMMON);
        when(qrcodeMapper.selectFishQrcodeByQrId(32L)).thenReturn(qr);

        AjaxResult result = controller.resolveQrcode(32L, null);

        Map<String, Object> data = (Map<String, Object>) result.get(AjaxResult.DATA_TAG);
        assertEquals("start", data.get("action"));
    }

    @Test
    public void legacyStartQrCanFinishSameVenue()
    {
        FishQrcode qr = qrcode(33L, VENUE_ID, FishQrcode.TYPE_START);
        FishOrder running = runningOrder(VENUE_ID);
        FishOrder finished = runningOrder(VENUE_ID);
        finished.setStatus(2);
        when(qrcodeMapper.selectFishQrcodeByQrId(33L)).thenReturn(qr);
        when(orderService.selectRunningOrder(USER_ID)).thenReturn(running);
        when(orderService.finishOrder(USER_ID)).thenReturn(finished);

        AjaxResult result = controller.finishOrder(qrBody(33L));

        assertSame(finished, result.get(AjaxResult.DATA_TAG));
    }

    @Test
    public void legacyEndQrCannotStart()
    {
        FishQrcode qr = qrcode(34L, VENUE_ID, FishQrcode.TYPE_END);
        when(qrcodeMapper.selectFishQrcodeByQrId(34L)).thenReturn(qr);

        ServiceException error = assertServiceException(() -> controller.startOrder(qrBody(34L)));

        assertTrue(error.getMessage().contains("离场码"));
        verify(orderService, never()).startOrder(any(Long.class), any(Long.class));
    }

    @Test
    public void scannedVenueMustMatchRunningOrder()
    {
        FishQrcode qr = qrcode(35L, 99L, FishQrcode.TYPE_START);
        when(qrcodeMapper.selectFishQrcodeByQrId(35L)).thenReturn(qr);
        when(orderService.selectRunningOrder(USER_ID)).thenReturn(runningOrder(VENUE_ID));

        ServiceException error = assertServiceException(() -> controller.finishOrder(qrBody(35L)));

        assertTrue(error.getMessage().contains("钓场不匹配"));
        verify(orderService, never()).finishOrder(USER_ID);
    }

    private Map<String, Object> qrBody(Long qrId)
    {
        Map<String, Object> body = new HashMap<>();
        body.put("qrId", qrId);
        return body;
    }

    private FishVenue venue(Long venueId)
    {
        FishVenue venue = new FishVenue();
        venue.setVenueId(venueId);
        venue.setStatus("0");
        return venue;
    }

    private FishOrder runningOrder(Long venueId)
    {
        FishOrder order = new FishOrder();
        order.setOrderId(101L);
        order.setUserId(USER_ID);
        order.setVenueId(venueId);
        order.setStatus(1);
        return order;
    }

    private FishQrcode qrcode(Long qrId, Long venueId, String type)
    {
        FishQrcode qr = new FishQrcode();
        qr.setQrId(qrId);
        qr.setVenueId(venueId);
        qr.setQrType(type);
        qr.setStatus("0");
        return qr;
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
        Field field = AppApiController.class.getDeclaredField(name);
        field.setAccessible(true);
        field.set(controller, value);
    }
}
