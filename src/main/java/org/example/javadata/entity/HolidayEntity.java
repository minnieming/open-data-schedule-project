package org.example.javadata.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "holiday")
public class HolidayEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String dateName;   // 공휴일명
    private String locdate;    // 날짜 (yyyyMMdd)
    private String isHoliday;  // 공휴일 여부 (Y/N)
    private Integer seq;       // 순번
}
