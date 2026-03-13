package org.example.javadata.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.javadata.entity.WeatherWarnEntity;
import org.example.javadata.io.WeatherWarnInput;
import org.example.javadata.repository.WeatherWarnRepository;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class WeatherWarnCollector implements PublicDataCollector {

    private final WeatherWarnInput weatherWarnInput;
    private final WeatherWarnRepository weatherWarnRepository;

    @Override
    public int collect() {
        try {
            return fetchAndSave(1, 100);
        } catch (Exception e) {
            log.error("[{}] 수집 실패", getCollectorName(), e);
            return 0;
        }
    }

    @Override
    public String getCollectorName() {
        return "WeatherWarnCollector";
    }

    @Transactional
    public int fetchAndSave(int pageNo, int numOfRows) throws Exception {
        String json = weatherWarnInput.callApi(pageNo, numOfRows);

        JSONObject root = new JSONObject(json);
        JSONObject response = root.optJSONObject("response");
        if (response == null) {
            log.warn("[{}] response 키가 없습니다. json={}", getCollectorName(), json);
            return 0;
        }

        JSONObject body = response.optJSONObject("body");
        if (body == null) {
            log.warn("[{}] 현재 발효 중인 기상특보가 없습니다.", getCollectorName());
            return 0;
        }

        JSONObject items = body.optJSONObject("items");
        if (items == null) {
            log.info("[{}] 현재 발효 중인 기상특보가 없습니다.", getCollectorName());
            return 0;
        }

        List<WeatherWarnEntity> entities = new ArrayList<>();
        Object itemObj = items.opt("item");

        if (itemObj instanceof JSONArray itemArray) {
            for (int i = 0; i < itemArray.length(); i++) {
                entities.add(toEntity(itemArray.getJSONObject(i)));
            }
        } else if (itemObj instanceof JSONObject itemSingle) {
            entities.add(toEntity(itemSingle));
        }

        if (entities.isEmpty()) {
            log.info("[{}] 수집된 기상특보 데이터가 없습니다.", getCollectorName());
            return 0;
        }

        weatherWarnRepository.saveAll(entities);
        log.info("[{}] 수집 완료: {} 개 저장", getCollectorName(), entities.size());
        return entities.size();
    }

    private WeatherWarnEntity toEntity(JSONObject item) {
        return WeatherWarnEntity.builder()
                .tmFc(item.optString("tmFc", null))
                .stnId(item.optString("stnId", null))
                .stnKo(item.optString("stnKo", null))
                .wrn(item.optString("wrn", null))
                .lvl(item.optString("lvl", null))
                .cmd(item.optString("cmd", null))
                .build();
    }
}
