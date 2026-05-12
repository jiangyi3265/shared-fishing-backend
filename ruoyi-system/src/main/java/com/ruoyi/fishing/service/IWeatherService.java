package com.ruoyi.fishing.service;

import java.util.Map;

public interface IWeatherService
{
    /**
     * 获取当前天气（气温、气压、湿度、风向、天气描述）
     * @param location 经纬度(如 "116.41,39.92") 或城市ID
     */
    Map<String, Object> getCurrentWeather(String location);
}
