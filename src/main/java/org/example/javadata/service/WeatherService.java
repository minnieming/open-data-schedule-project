package org.example.javadata.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.javadata.dto.WeatherReqDTO;
import org.example.javadata.entity.WeatherHistoryEntity;
import org.example.javadata.entity.WeatherLatestEntity;
import org.example.javadata.io.WeatherInput;
import org.example.javadata.repository.WeatherHistoryRepository;
import org.example.javadata.repository.WeatherLatestRepository;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class WeatherService {

    private final WeatherInput weatherInput;
    private final WeatherHistoryRepository historyRepository;
    private final WeatherLatestRepository latestRepository;

    @Transactional
    public int fetchAndSave(int pageNo, int numOfRows) throws Exception {

        String json = weatherInput.callApi(pageNo, numOfRows);

        JSONObject root = new JSONObject(json);
        JSONArray bodyArray = root.optJSONArray("body");

        if (bodyArray == null) {
            throw new RuntimeException("응답에 body 배열이 없습니다. json=" + json);
        }

        List<WeatherHistoryEntity> histories = new ArrayList<>();
        Map<String, WeatherLatestEntity> latestByRegion = new LinkedHashMap<>();
        int skippedCount = 0;

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

            WeatherHistoryEntity history = WeatherHistoryEntity.from(dto);
            if (history.getCurrentTemp() == null) {
                skippedCount++;
                log.warn("Skip weather row due to missing current temperature. regionCode={}, regionName={}, observedTime={}, forecastTime={}",
                        dto.getSTDG_SGG_CD(), dto.getSGG_NM(), dto.getPRCON_CRTR_TM(), dto.getFRCST_CRTR_TM());
                continue;
            }

            histories.add(history);

            String regionCode = dto.getSTDG_SGG_CD();
            if (regionCode != null && !regionCode.isBlank()) {
                latestByRegion.put(regionCode, WeatherLatestEntity.from(dto));
            }
        }

        if (histories.isEmpty()) {
            throw new IllegalStateException("저장 가능한 날씨 데이터가 없습니다. 모든 응답 항목의 NOW_AIRTP 값이 비어 있습니다.");
        }

        historyRepository.saveAll(histories);

        if (!latestByRegion.isEmpty()) {
            for (String regionCode : latestByRegion.keySet()) {
                latestRepository.deleteByRegionCode(regionCode);
            }
            latestRepository.saveAll(new ArrayList<>(latestByRegion.values()));
        }

        if (skippedCount > 0) {
            log.warn("Skipped {} weather rows because current temperature was missing.", skippedCount);
        }

        return histories.size();
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
