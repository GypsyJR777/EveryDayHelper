package com.github.gypsyjr777.model.weather;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Wind {
    private float speed;
    private Integer deg;
    private float gust;
}
