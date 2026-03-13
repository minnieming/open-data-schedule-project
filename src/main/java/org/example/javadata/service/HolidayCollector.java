package org.example.javadata.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.javadata.entity.HolidayEntity;
import org.example.javadata.io.HolidayInput;
import org.example.javadata.repository.HolidayRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Component
@Slf4j
@RequiredArgsConstructor
public class HolidayCollector implements PublicDataCollector {

    private final HolidayInput holidayInput;
    private final HolidayRepository holidayRepository;

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
        return "HolidayCollector";
    }

    @Transactional
    public int fetchAndSave(int pageNo, int numOfRows) throws Exception {
        String xml = holidayInput.callApi(pageNo, numOfRows);

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));

        NodeList itemNodes = doc.getElementsByTagName("item");
        if (itemNodes.getLength() == 0) {
            log.info("[{}] 이번 달 공휴일/특일 데이터가 없습니다.", getCollectorName());
            return 0;
        }

        List<HolidayEntity> entities = new ArrayList<>();
        Set<String> locdates = new LinkedHashSet<>();

        for (int i = 0; i < itemNodes.getLength(); i++) {
            Element item = (Element) itemNodes.item(i);
            String locdate = getTagValue("locdate", item);
            if (locdate != null) locdates.add(locdate);

            entities.add(HolidayEntity.builder()
                    .dateName(getTagValue("dateName", item))
                    .locdate(locdate)
                    .isHoliday(getTagValue("isHoliday", item))
                    .seq(parseIntOrNull(getTagValue("seq", item)))
                    .build());
        }

        for (String locdate : locdates) {
            holidayRepository.deleteByLocdate(locdate);
        }

        holidayRepository.saveAll(entities);
        log.info("[{}] 수집 완료: {} 개 저장", getCollectorName(), entities.size());
        return entities.size();
    }

    private String getTagValue(String tag, Element element) {
        NodeList nodes = element.getElementsByTagName(tag);
        if (nodes.getLength() == 0) return null;
        return nodes.item(0).getTextContent();
    }

    private Integer parseIntOrNull(String value) {
        if (value == null || value.isBlank()) return null;
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
