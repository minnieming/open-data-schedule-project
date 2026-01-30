package org.example.javadata.service;

import lombok.RequiredArgsConstructor;
import org.example.javadata.dto.WeatherReqDTO;
import org.example.javadata.entity.WeatherHistoryEntity;
import org.example.javadata.entity.WeatherLatestEntity;
import org.example.javadata.io.WeatherInput;
import org.example.javadata.repository.WeatherHistoryRepository;
import org.example.javadata.repository.WeatherLatestRepository;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WeatherService {

    private final WeatherInput weatherInput;
    private final WeatherHistoryRepository historyRepository;
    private final WeatherLatestRepository latestRepository;

    public int fetchAndSave(int pageNo, int numOfRows) throws Exception {

        String json = weatherInput.callApi(pageNo, numOfRows);

        JSONObject root = new JSONObject(json);
        JSONArray bodyArray = root.optJSONArray("body");

        if (bodyArray == null) {
            throw new RuntimeException("응답에 body 배열이 없습니다. json=" + json);
        }

        List<WeatherHistoryEntity> histories = new ArrayList<>();
        List<WeatherLatestEntity> latestList = new ArrayList<>();

        for (int i = 0; i < bodyArray.length(); i++) {
            JSONObject item = bodyArray.getJSONObject(i);

            WeatherReqDTO dto = WeatherReqDTO.builder()
                    .STDG_SGG_CD(item.optString("STDG_SGG_CD", null))
                    .SGG_NM(item.optString("SGG_NM", null))
                    .SKY_STTS(item.optString("SKY_STTS", null))
                    .N1HR_RN(item.optString("N1HR_RN", null))
                    .HMTY_(optIntOrNull(item, "HMTY_"))
                    .PRCON_CRTR_TM(item.optString("PRCON_CRTR_TM", null))
                    .POR(optIntOrNull(item, "POR"))
                    .YMAP_CRTS(optIntOrNull(item, "YMAP_CRTS"))
                    .N3HS_AIRTP(item.optString("N3HS_AIRTP", null))
                    .PCPTTN_SHP(item.optString("PCPTTN_SHP", null))
                    .NOW_AIRTP(item.optString("NOW_AIRTP", null))
                    .FRCST_CRTR_TM(item.optString("FRCST_CRTR_TM", null))
                    .XMAP_CRTS(optIntOrNull(item, "XMAP_CRTS"))
                    .build();

            // history: 무조건 쌓기
            histories.add(WeatherHistoryEntity.from(dto));

            // latest: regionCode 기준 1개 유지
            String regionCode = dto.getSTDG_SGG_CD();
            if (regionCode != null && !regionCode.isBlank()) {
                latestRepository.deleteByRegionCode(regionCode);
            }
            latestList.add(WeatherLatestEntity.from(dto));
        }

        historyRepository.saveAll(histories);
        latestRepository.saveAll(latestList);

        return bodyArray.length();
    }

    private Integer optIntOrNull(JSONObject obj, String key) {
        if (!obj.has(key)) return null;
        Object v = obj.opt(key);
        if (v == null) return null;
        if (v instanceof Number n) return n.intValue();
        String s = String.valueOf(v).trim();
        if (s.isEmpty() || "null".equalsIgnoreCase(s)) return null;
        return Integer.valueOf(s);
    }
}