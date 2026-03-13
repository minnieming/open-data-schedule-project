package org.example.javadata.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "tourism_visitor")
public class TourismVisitorEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String baseYmd;   // 기준날짜
    private String areaCd;    // 지역코드
    private String areaNm;    // 지역명
    private String touDivNm;  // 관광객구분명
    private Long touNum;      // 방문자수
}
