package com.github.GypsyJR777.model.weather;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Weather {
    private Integer id;
    private String main;
    private String description;
    private String icon;
}
