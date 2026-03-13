package org.example.javadata.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "weather_warn")
public class WeatherWarnEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String tmFc;      // 발표시각
    private String stnId;     // 지점코드
    private String stnKo;     // 지점명
    private String wrn;       // 특보코드
    private String lvl;       // 특보수준
    private String cmd;       // 특보명

    @CreationTimestamp
    private LocalDateTime createdAt;
}
