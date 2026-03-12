package org.example.javadata.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.javadata.dto.WeatherReqDTO;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "weather_history")
public class WeatherHistoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 쉬운 이름으로 저장
    private String regionCode;          // STDG_SGG_CD
    private String regionName;          // SGG_NM

    private String skyStatus;           // SKY_STTS
    @Column(nullable = true)
    private Double rainAmount1h;        // N1HR_RN (문자열인데 숫자일 때가 많음)
    @Column(nullable = true)
    private Integer humidity;           // HMTY_
    private String observedTime;        // PRCON_CRTR_TM
    @Column(nullable = true)
    private Integer rainProbability;    // POR

    @Column(nullable = true)
    private Integer yCoord;             // YMAP_CRTS
    @Column(nullable = true)
    private Double tempAfter3h;         // N3HS_AIRTP (null 가능)
    private String rainType;            // PCPTTN_SHP
    @Column(nullable = true)
    private Double currentTemp;         // NOW_AIRTP

    private String forecastTime;        // FRCST_CRTR_TM
    @Column(nullable = true)
    private Integer xCoord;             // XMAP_CRTS

    public static WeatherHistoryEntity from(WeatherReqDTO dto) {
        return WeatherHistoryEntity.builder()
                .regionCode(dto.getSTDG_SGG_CD())
                .regionName(dto.getSGG_NM())
                .skyStatus(dto.getSKY_STTS())
                .rainAmount1h(toDouble(dto.getN1HR_RN()))
                .humidity(dto.getHMTY_())
                .observedTime(dto.getPRCON_CRTR_TM())
                .rainProbability(dto.getPOR())
                .yCoord(dto.getYMAP_CRTS())
                .tempAfter3h(toDouble(dto.getN3HS_AIRTP()))
                .rainType(dto.getPCPTTN_SHP())
                .currentTemp(toDouble(dto.getNOW_AIRTP()))
                .forecastTime(dto.getFRCST_CRTR_TM())
                .xCoord(dto.getXMAP_CRTS())
                .build();
    }

    private static Double toDouble(String v) {
        if (v == null) return null;
        String s = v.trim();
        if (s.isEmpty() || "null".equalsIgnoreCase(s)) return null;
        return Double.valueOf(s);
    }
}
