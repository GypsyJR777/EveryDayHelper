package com.github.gypsyjr777.service;

import com.github.gypsyjr777.config.WeatherConfig;
import com.github.gypsyjr777.model.weather.WeatherResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class WeatherService {
    private WeatherConfig weatherConfig;

    @Autowired
    public WeatherService(WeatherConfig weatherConfig) {
        this.weatherConfig = weatherConfig;
    }

    public String getWeatherByCity(String city) {
        String query = "?q=" + city + "&appid=" + weatherConfig.getKey() + "&units=metric&lang=ru";
        RestTemplate restTemplate = new RestTemplate();
        WeatherResult weatherResult;
        try {
            weatherResult = restTemplate.getForObject(weatherConfig.getUrl() + query, WeatherResult.class);
        } catch (HttpClientErrorException e) {
            return "Такого города нет, проверьте правильность введенных данных";
        }

        return weatherResult.toString();
    }
}
