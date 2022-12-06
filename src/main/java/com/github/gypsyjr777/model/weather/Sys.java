package com.github.gypsyjr777.model.weather;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Sys {
    private Integer type;
    private Integer id;
    private String country;
    private Long sunrise;
    private Long sunset;
}
