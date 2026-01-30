package org.example.javadata.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class WeatherReqDTO {

    private String STDG_SGG_CD;   // 지역코드
    private String SGG_NM;        // 지역명

    private String SKY_STTS;
    private String N1HR_RN;
    private Integer HMTY_;
    private String PRCON_CRTR_TM;
    private Integer POR;

    private Integer YMAP_CRTS;
    private String N3HS_AIRTP;    // null 가능
    private String PCPTTN_SHP;
    private String NOW_AIRTP;

    private String FRCST_CRTR_TM;
    private Integer XMAP_CRTS;
}
