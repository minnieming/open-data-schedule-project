package org.example.javadata.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "weather_history_outbox")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WeatherHistoryOutboxEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String regionCode;
    private String regionName;
    private String skyStatus;
    private String observedTime;
    private String forecastTime;
    private String rainType;
    private Double currentTemp;
    private Integer humidity;
    private Integer rainProbability;
    private Double rainAmount1h;
    private Double tempAfter3h;
    private Integer xCoord;
    private Integer yCoord;

    @Builder.Default
    private String status = "PENDING"; // PENDING, DONE, FAILED

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    public void markDone() {
        this.status = "DONE";
    }

    public void markFailed() {
        this.status = "FAILED";
    }

    public static WeatherHistoryOutboxEntity from(WeatherHistoryEntity e) {
        return WeatherHistoryOutboxEntity.builder()
                .regionCode(e.getRegionCode())
                .regionName(e.getRegionName())
                .skyStatus(e.getSkyStatus())
                .observedTime(e.getObservedTime())
                .forecastTime(e.getForecastTime())
                .rainType(e.getRainType())
                .currentTemp(e.getCurrentTemp())
                .humidity(e.getHumidity())
                .rainProbability(e.getRainProbability())
                .rainAmount1h(e.getRainAmount1h())
                .tempAfter3h(e.getTempAfter3h())
                .xCoord(e.getXCoord())
                .yCoord(e.getYCoord())
                .build();
    }
}
