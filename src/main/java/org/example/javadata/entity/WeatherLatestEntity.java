package org.example.javadata.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.javadata.dto.WeatherReqDTO;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "weather_latest")
public class WeatherLatestEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

    public WeatherLatestEntity(WeatherReqDTO dto) {
        this.regionCode = dto.getRegionCode();
        this.regionName = dto.getRegionName();
        this.rainProbability = dto.getRainProbability();
        this.humidity = dto.getHumidity();
        this.skyStatus = dto.getSkyStatus();
        this.currentTemp = dto.getCurrentTemp();
        this.tempAfter3h = dto.getTempAfter3h();
        this.rainAmount1h = dto.getRainAmount1h();
        this.rainType = dto.getRainType();
        this.forecastTime = dto.getForecastTime();
        this.observedTime = dto.getObservedTime();
        this.xCoord = dto.getXCoord();
        this.yCoord = dto.getYCoord();
    }
}