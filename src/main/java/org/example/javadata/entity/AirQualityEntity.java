package org.example.javadata.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "air_quality")
public class AirQualityEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String sidoName;       // 시도명
    private String stationName;    // 측정소명
    private String dataTime;       // 측정일시
    private String pm10Value;      // 미세먼지 (PM10)
    private String pm25Value;      // 초미세먼지 (PM2.5)
    private String o3Value;        // 오존
    private String coValue;        // 일산화탄소
    private String no2Value;       // 이산화질소
    private String so2Value;       // 아황산가스
    private String khaiValue;      // 통합대기환경지수
    private String khaiGrade;      // 통합대기환경등급
}
