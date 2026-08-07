package com.ruoyi.fishing.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.lang.reflect.Field;
import java.util.Map;
import org.junit.Test;

public class WeatherServiceImplTest
{
    @Test
    public void missingApiKeyReturnsUnavailableStateInsteadOfFakeWeather() throws Exception
    {
        WeatherServiceImpl service = new WeatherServiceImpl();
        Field apiKey = WeatherServiceImpl.class.getDeclaredField("apiKey");
        apiKey.setAccessible(true);
        apiKey.set(service, "");

        Map<String, Object> weather = service.getCurrentWeather("116.41,39.92");

        assertEquals(Boolean.FALSE, weather.get("available"));
        assertEquals("天气服务暂未配置", weather.get("message"));
        assertFalse(weather.containsKey("temp"));
        assertFalse(weather.containsKey("mock"));
    }
}
