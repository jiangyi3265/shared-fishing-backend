package com.ruoyi.fishing.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import com.ruoyi.fishing.mapper.FishCardGameMapper;
import com.ruoyi.fishing.mapper.FishCatchRecordMapper;
import com.ruoyi.fishing.service.IFishUserService;

public class FishCardGameServiceImplTest
{
    private FishCardGameServiceImpl service;
    private FishCardGameMapper mapper;

    @Before
    public void setUp() throws Exception
    {
        service = new FishCardGameServiceImpl();
        mapper = mock(FishCardGameMapper.class);
        setField("mapper", mapper);
        setField("catchMapper", mock(FishCatchRecordMapper.class));
        setField("userService", mock(IFishUserService.class));
    }

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

    @Test
    @SuppressWarnings("unchecked")
    public void sixActiveSpeciesCountForRewardAndOtherCardsStayUnavailable()
    {
        Long campaignId = 8L;
        Map<String, Object> campaign = new HashMap<>();
        campaign.put("campaignId", campaignId);
        campaign.put("startTime", new Date(System.currentTimeMillis() - 60_000L));
        campaign.put("endTime", new Date(System.currentTimeMillis() + 60_000L));
        campaign.put("rewardCents", 6600);

        List<Map<String, Object>> active = species(1, 6, "0");
        List<Map<String, Object>> display = new ArrayList<>(active);
        display.addAll(species(7, 10, "1"));
        when(mapper.selectActiveCampaign()).thenReturn(campaign);
        when(mapper.selectSpecies(campaignId)).thenReturn(active);
        when(mapper.selectSpeciesForDisplay(campaignId)).thenReturn(display);
        when(mapper.selectOpenRound(campaignId, 3L)).thenReturn(null);
        when(mapper.selectMaxRoundNo(campaignId, 3L)).thenReturn(0);
        when(mapper.selectRanking(campaignId)).thenReturn(Collections.emptyList());

        Map<String, Object> game = service.getMyGame(3L);
        Map<String, Object> round = (Map<String, Object>) game.get("round");
        List<Map<String, Object>> cards = (List<Map<String, Object>>) game.get("cards");

        assertEquals(6, round.get("totalCount"));
        assertEquals(10, cards.size());
        assertFalse((Boolean) cards.get(9).get("available"));
        assertEquals("unavailable", cards.get(9).get("cardStatus"));
    }

    private List<Map<String, Object>> species(int from, int to, String status)
    {
        List<Map<String, Object>> result = new ArrayList<>();
        for (int i = from; i <= to; i++)
        {
            Map<String, Object> fish = new HashMap<>();
            fish.put("speciesId", (long) i);
            fish.put("speciesName", "fish-" + i);
            fish.put("status", status);
            result.add(fish);
        }
        return result;
    }

    private void setField(String name, Object value) throws Exception
    {
        Field field = FishCardGameServiceImpl.class.getDeclaredField(name);
        field.setAccessible(true);
        field.set(service, value);
    }
}
