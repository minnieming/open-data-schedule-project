package org.example.javadata.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.javadata.dto.WeatherReqDTO;
import org.example.javadata.entity.WeatherHistoryEntity;
import org.example.javadata.entity.WeatherHistoryOutboxEntity;
import org.example.javadata.entity.WeatherLatestEntity;
import org.example.javadata.event.WeatherCollectedEvent;
import org.example.javadata.io.WeatherInput;
import org.example.javadata.repository.WeatherHistoryOutboxRepository;
import org.example.javadata.repository.WeatherLatestRepository;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class WeatherCollector implements PublicDataCollector {

    private final WeatherInput weatherInput;
    private final WeatherHistoryOutboxRepository outboxRepository;
    private final WeatherLatestRepository latestRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
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
        return "WeatherCollector";
    }

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
                log.warn("[{}] Skip weather row due to missing current temperature. regionCode={}, regionName={}, observedTime={}, forecastTime={}",
                        getCollectorName(), dto.getSTDG_SGG_CD(), dto.getSGG_NM(), dto.getPRCON_CRTR_TM(), dto.getFRCST_CRTR_TM());
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

        // history는 outbox에 저장 → 트랜잭션 커밋 후 이벤트로 별도 처리
        List<WeatherHistoryOutboxEntity> outboxEntities = histories.stream()
                .map(WeatherHistoryOutboxEntity::from)
                .toList();
        List<WeatherHistoryOutboxEntity> savedOutbox = outboxRepository.saveAll(outboxEntities);
        List<Long> outboxIds = savedOutbox.stream().map(WeatherHistoryOutboxEntity::getId).toList();

        // latest는 핵심 데이터 → 같은 트랜잭션에서 처리
        if (!latestByRegion.isEmpty()) {
            for (String regionCode : latestByRegion.keySet()) {
                latestRepository.deleteByRegionCode(regionCode);
            }
            latestRepository.saveAll(new ArrayList<>(latestByRegion.values()));
        }

        // latest 트랜잭션 커밋 후 history 저장 이벤트 발행
        eventPublisher.publishEvent(new WeatherCollectedEvent(outboxIds));

        if (skippedCount > 0) {
            log.warn("[{}] Skipped {} weather rows because current temperature was missing.",
                    getCollectorName(), skippedCount);
        }

        log.info("[{}] 수집 완료: {} 개 저장", getCollectorName(), histories.size());
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
