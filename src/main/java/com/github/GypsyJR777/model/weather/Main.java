package com.github.GypsyJR777.model.weather;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Main {
    private float temp;
    private float feels_like;
    private float temp_min;
    private float temp_max;
    private float pressure;
    private Integer humidity;
    private Integer sea_level;
    private Integer grnd_level;
}
