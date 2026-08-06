package com.ruoyi.fishing.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import org.junit.Test;

public class FishCardGameServiceImplTest
{
    @Test
    public void acceptsLegacyDateValue()
    {
        Date value = new Date(1_700_000_000_000L);

        assertSame(value, FishCardGameServiceImpl.normalizeCampaignDate(value));
    }

    @Test
    public void convertsLocalDateTimeReturnedByMysqlDriver()
    {
        LocalDateTime value = LocalDateTime.of(2026, 8, 6, 8, 30, 15);
        Date expected = Date.from(value.atZone(ZoneId.systemDefault()).toInstant());

        assertEquals(expected, FishCardGameServiceImpl.normalizeCampaignDate(value));
    }

    @Test
    public void convertsLocalDateReturnedByMysqlDriver()
    {
        LocalDate value = LocalDate.of(2026, 8, 6);
        Date expected = Date.from(value.atStartOfDay(ZoneId.systemDefault()).toInstant());

        assertEquals(expected, FishCardGameServiceImpl.normalizeCampaignDate(value));
    }
}
