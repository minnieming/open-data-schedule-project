package org.example.javadata.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WeatherReqDTO {
    private String regionCode;
    private String regionName;

    private int rainProbability;
    private int humidity;

    private String skyStatus;
    private double currentTemp;
    private double tempAfter3h;

    private double rainAmount1h;
    private String rainType;

    private String forecastTime;
    private String observedTime;

    private int xCoord;
    private int yCoord;
}
