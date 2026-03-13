package org.example.javadata.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.javadata.entity.AirQualityEntity;
import org.example.javadata.io.AirQualityInput;
import org.example.javadata.repository.AirQualityRepository;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class AirQualityCollector implements PublicDataCollector {

    private final AirQualityInput airQualityInput;
    private final AirQualityRepository airQualityRepository;

    @Override
    public int collect() {
        try {
            return fetchAndSave(1, 500);
        } catch (Exception e) {
            log.error("[{}] 수집 실패", getCollectorName(), e);
            return 0;
        }
    }

    @Override
    public String getCollectorName() {
        return "AirQualityCollector";
    }

    @Transactional
    public int fetchAndSave(int pageNo, int numOfRows) throws Exception {
        String json = airQualityInput.callApi(pageNo, numOfRows);

        JSONObject root = new JSONObject(json);
        JSONObject response = root.optJSONObject("response");
        if (response == null) {
            log.warn("[{}] response 키가 없습니다.", getCollectorName());
            return 0;
        }

        JSONObject body = response.optJSONObject("body");
        if (body == null) {
            log.warn("[{}] body 키가 없습니다.", getCollectorName());
            return 0;
        }

        JSONArray items = body.optJSONArray("items");
        if (items == null || items.length() == 0) {
            log.warn("[{}] 수집된 대기오염 데이터가 없습니다.", getCollectorName());
            return 0;
        }

        List<AirQualityEntity> entities = new ArrayList<>();
        for (int i = 0; i < items.length(); i++) {
            JSONObject item = items.getJSONObject(i);
            entities.add(AirQualityEntity.builder()
                    .sidoName(item.optString("sidoName", null))
                    .stationName(item.optString("stationName", null))
                    .dataTime(item.optString("dataTime", null))
                    .pm10Value(item.optString("pm10Value", null))
                    .pm25Value(item.optString("pm25Value", null))
                    .o3Value(item.optString("o3Value", null))
                    .coValue(item.optString("coValue", null))
                    .no2Value(item.optString("no2Value", null))
                    .so2Value(item.optString("so2Value", null))
                    .khaiValue(item.optString("khaiValue", null))
                    .khaiGrade(item.optString("khaiGrade", null))
                    .build());
        }

        airQualityRepository.saveAll(entities);
        log.info("[{}] 수집 완료: {} 개 저장", getCollectorName(), entities.size());
        return entities.size();
    }
}
