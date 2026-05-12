package com.ruoyi.fishing.service.impl;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.ruoyi.fishing.service.IWeatherService;

@Service
public class WeatherServiceImpl implements IWeatherService
{
    private static final Logger log = LoggerFactory.getLogger(WeatherServiceImpl.class);

    @Value("${weather.qweather.key:}")
    private String apiKey;

    @Value("${weather.qweather.baseUrl:https://devapi.qweather.com}")
    private String baseUrl;

    private final RestTemplate rest = new RestTemplate();

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> getCurrentWeather(String location)
    {
        if (apiKey == null || apiKey.isEmpty()) {
            return mockWeather();
        }
        try {
            String url = baseUrl + "/v7/weather/now?location=" + location + "&key=" + apiKey;
            Map<String, Object> resp = rest.getForObject(url, Map.class);
            if (resp != null && "200".equals(resp.get("code"))) {
                Map<String, Object> now = (Map<String, Object>) resp.get("now");
                Map<String, Object> result = new HashMap<>();
                result.put("temp", now.get("temp"));
                result.put("text", now.get("text"));
                result.put("windDir", now.get("windDir"));
                result.put("windScale", now.get("windScale"));
                result.put("humidity", now.get("humidity"));
                result.put("pressure", now.get("pressure"));
                result.put("feelsLike", now.get("feelsLike"));
                return result;
            }
        } catch (Exception e) {
            log.warn("[weather] API call failed: {}", e.getMessage());
        }
        return mockWeather();
    }

    private Map<String, Object> mockWeather() {
        Map<String, Object> m = new HashMap<>();
        m.put("temp", "26");
        m.put("text", "多云");
        m.put("windDir", "东南风");
        m.put("windScale", "2");
        m.put("humidity", "65");
        m.put("pressure", "1013");
        m.put("feelsLike", "28");
        m.put("mock", true);
        return m;
    }
}
