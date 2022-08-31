package com.github.GypsyJR777.service;

import com.github.GypsyJR777.config.WeatherConfig;
import com.github.GypsyJR777.model.weather.WeatherResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class WeatherParser {
    private WeatherConfig weatherConfig;

    @Autowired
    public WeatherParser(WeatherConfig weatherConfig) {
        this.weatherConfig = weatherConfig;
    }

    public WeatherResult getWeatherByCity(String city) {
        String query = "?q=" + city + "&appid=" + weatherConfig.getKey() + "&units=metric&lang=ru";
        RestTemplate restTemplate = new RestTemplate();

        return restTemplate.getForObject(weatherConfig.getUrl() + query, WeatherResult.class);
    }
}
