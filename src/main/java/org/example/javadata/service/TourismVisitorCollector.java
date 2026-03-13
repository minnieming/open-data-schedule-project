package org.example.javadata.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.javadata.entity.TourismVisitorEntity;
import org.example.javadata.io.TourismInput;
import org.example.javadata.repository.TourismVisitorRepository;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class TourismVisitorCollector implements PublicDataCollector {

    private final TourismInput tourismInput;
    private final TourismVisitorRepository tourismVisitorRepository;

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
        return "TourismVisitorCollector";
    }

    @Transactional
    public int fetchAndSave(int pageNo, int numOfRows) throws Exception {
        String json = tourismInput.callApi(pageNo, numOfRows);

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

        JSONObject items = body.optJSONObject("items");
        if (items == null) {
            log.warn("[{}] 수집된 관광 데이터가 없습니다.", getCollectorName());
            return 0;
        }

        List<TourismVisitorEntity> entities = new ArrayList<>();
        Object itemObj = items.opt("item");

        if (itemObj instanceof JSONArray itemArray) {
            for (int i = 0; i < itemArray.length(); i++) {
                entities.add(toEntity(itemArray.getJSONObject(i)));
            }
        } else if (itemObj instanceof JSONObject itemSingle) {
            entities.add(toEntity(itemSingle));
        }

        if (entities.isEmpty()) {
            log.warn("[{}] 수집된 관광 데이터가 없습니다.", getCollectorName());
            return 0;
        }

        tourismVisitorRepository.saveAll(entities);
        log.info("[{}] 수집 완료: {} 개 저장", getCollectorName(), entities.size());
        return entities.size();
    }

    private TourismVisitorEntity toEntity(JSONObject item) {
        return TourismVisitorEntity.builder()
                .baseYmd(item.optString("baseYmd", null))
                .areaCd(item.optString("areaCd", null))
                .areaNm(item.optString("areaNm", null))
                .touDivNm(item.optString("touDivNm", null))
                .touNum(item.optLong("touNum", 0L))
                .build();
    }
}
