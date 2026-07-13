package com.ruoyi.web.controller.fishing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import org.junit.Test;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.fishing.domain.FishStockingRecord;
import com.ruoyi.fishing.domain.FishVenue;
import com.ruoyi.fishing.mapper.FishStockingRecordMapper;
import com.ruoyi.fishing.mapper.FishVenueMapper;
import com.ruoyi.fishing.service.impl.FishStockingServiceImpl;
import com.ruoyi.fishing.service.impl.FishVenueServiceImpl;

public class FishingValidationServiceTest
{
    @Test
    public void venueCoordinatesMustBeProvidedAsPair() throws Exception
    {
        FishVenueServiceImpl service = venueService(mock(FishVenueMapper.class));
        FishVenue venue = new FishVenue();
        venue.setVenueId(1L);
        venue.setLatitude(new BigDecimal("30.1234567"));

        ServiceException error = assertServiceException(() -> service.updateFishVenue(venue));

        assertTrue(error.getMessage().contains("同时填写"));
    }

    @Test
    public void zeroCoordinatePairIsRejected() throws Exception
    {
        FishVenueServiceImpl service = venueService(mock(FishVenueMapper.class));
        FishVenue venue = new FishVenue();
        venue.setVenueId(1L);
        venue.setLatitude(BigDecimal.ZERO);
        venue.setLongitude(BigDecimal.ZERO);

        ServiceException error = assertServiceException(() -> service.updateFishVenue(venue));

        assertTrue(error.getMessage().contains("不能同时为 0"));
    }

    @Test
    public void coordinatePairCanBeClearedTogether() throws Exception
    {
        FishVenueMapper mapper = mock(FishVenueMapper.class);
        FishVenueServiceImpl service = venueService(mapper);
        FishVenue venue = new FishVenue();
        venue.setVenueId(1L);
        when(mapper.updateFishVenue(venue)).thenReturn(1);

        assertEquals(1, service.updateFishVenue(venue));
        verify(mapper).updateFishVenue(venue);
    }

    @Test
    public void stockingInsertRejectsMissingVenueBeforeDatabase() throws Exception
    {
        FishStockingServiceImpl service = new FishStockingServiceImpl();
        setField(service, "mapper", mock(FishStockingRecordMapper.class));
        setField(service, "venueMapper", mock(FishVenueMapper.class));
        FishStockingRecord record = validStockingRecord();

        ServiceException error = assertServiceException(() -> service.insertRecord(record));

        assertTrue(error.getMessage().contains("请选择所属钓场"));
    }

    @Test
    public void stockingInsertAcceptsActiveVenue() throws Exception
    {
        FishStockingRecordMapper stockingMapper = mock(FishStockingRecordMapper.class);
        FishVenueMapper venueMapper = mock(FishVenueMapper.class);
        FishStockingServiceImpl service = new FishStockingServiceImpl();
        setField(service, "mapper", stockingMapper);
        setField(service, "venueMapper", venueMapper);
        FishVenue venue = new FishVenue();
        venue.setVenueId(8L);
        venue.setStatus("0");
        FishStockingRecord record = validStockingRecord();
        record.setVenueId(8L);
        when(venueMapper.selectFishVenueByVenueId(8L)).thenReturn(venue);
        when(stockingMapper.insertRecord(record)).thenReturn(1);

        assertEquals(1, service.insertRecord(record));
        verify(stockingMapper).insertRecord(record);
    }

    @Test
    public void stockingInsertRejectsBlankSpecies() throws Exception
    {
        FishStockingServiceImpl service = stockingService();
        FishStockingRecord record = validStockingRecord();
        record.setFishSpecies("  ");

        ServiceException error = assertServiceException(() -> service.insertRecord(record));

        assertTrue(error.getMessage().contains("鱼种"));
    }

    @Test
    public void stockingInsertRejectsNonPositiveWeight() throws Exception
    {
        FishStockingServiceImpl service = stockingService();
        FishStockingRecord record = validStockingRecord();
        record.setWeightJin(BigDecimal.ZERO);

        ServiceException error = assertServiceException(() -> service.insertRecord(record));

        assertTrue(error.getMessage().contains("必须大于 0"));
    }

    @Test
    public void stockingInsertRejectsNegativeFishCount() throws Exception
    {
        FishStockingServiceImpl service = stockingService();
        FishStockingRecord record = validStockingRecord();
        record.setFishCount(-1);

        ServiceException error = assertServiceException(() -> service.insertRecord(record));

        assertTrue(error.getMessage().contains("不能小于 0"));
    }

    private FishVenueServiceImpl venueService(FishVenueMapper mapper) throws Exception
    {
        FishVenueServiceImpl service = new FishVenueServiceImpl();
        setField(service, "venueMapper", mapper);
        return service;
    }

    private FishStockingServiceImpl stockingService() throws Exception
    {
        FishStockingServiceImpl service = new FishStockingServiceImpl();
        setField(service, "mapper", mock(FishStockingRecordMapper.class));
        setField(service, "venueMapper", mock(FishVenueMapper.class));
        return service;
    }

    private FishStockingRecord validStockingRecord()
    {
        FishStockingRecord record = new FishStockingRecord();
        record.setFishSpecies("鲤鱼");
        record.setWeightJin(new BigDecimal("10.5"));
        record.setFishCount(2);
        return record;
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

    private void setField(Object target, String name, Object value) throws Exception
    {
        Field field = target.getClass().getDeclaredField(name);
        field.setAccessible(true);
        field.set(target, value);
    }
}
