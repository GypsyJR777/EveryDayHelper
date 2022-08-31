package com.github.GypsyJR777.model.weather;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WeatherResult {
    private Coord coord;
    private Weather[] weather;
    private String base;
    private Main main;
    private Integer visibility;
    private Wind wind;
    private Clouds clouds;
    private Long dt;
    private Sys sys;
    private Integer timezone;
    private Integer id;
    private String name;
    private Integer cod;

    @Override
    public String toString() {
        return "Погода в городе " + name + ": " + weather[0].getDescription() + ", температура - " +  (int) main.getTemp() +
                ", чувствуется на: " + (int) main.getFeels_like();
    }
}
