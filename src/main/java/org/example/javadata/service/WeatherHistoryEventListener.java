package org.example.javadata.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.javadata.entity.WeatherHistoryEntity;
import org.example.javadata.entity.WeatherHistoryOutboxEntity;
import org.example.javadata.event.WeatherCollectedEvent;
import org.example.javadata.repository.WeatherHistoryOutboxRepository;
import org.example.javadata.repository.WeatherHistoryRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class WeatherHistoryEventListener {

    private final WeatherHistoryOutboxRepository outboxRepository;
    private final WeatherHistoryRepository historyRepository;

    // latest 트랜잭션이 커밋된 이후에 실행 → 별도 트랜잭션으로 history 저장
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handle(WeatherCollectedEvent event) {
        List<WeatherHistoryOutboxEntity> outboxList =
                outboxRepository.findByIdInAndStatus(event.getOutboxIds(), "PENDING");

        log.info("[WeatherHistoryEventListener] outbox 처리 시작 - {} 건", outboxList.size());

        try {
            List<WeatherHistoryEntity> histories = outboxList.stream()
                    .map(o -> WeatherHistoryEntity.builder()
                            .regionCode(o.getRegionCode())
                            .regionName(o.getRegionName())
                            .skyStatus(o.getSkyStatus())
                            .observedTime(o.getObservedTime())
                            .forecastTime(o.getForecastTime())
                            .rainType(o.getRainType())
                            .currentTemp(o.getCurrentTemp())
                            .humidity(o.getHumidity())
                            .rainProbability(o.getRainProbability())
                            .rainAmount1h(o.getRainAmount1h())
                            .tempAfter3h(o.getTempAfter3h())
                            .xCoord(o.getXCoord())
                            .yCoord(o.getYCoord())
                            .build())
                    .toList();

            historyRepository.saveAll(histories);
            outboxList.forEach(WeatherHistoryOutboxEntity::markDone);
            log.info("[WeatherHistoryEventListener] history 저장 완료 - {} 건", histories.size());

        } catch (Exception e) {
            outboxList.forEach(WeatherHistoryOutboxEntity::markFailed);
            log.error("[WeatherHistoryEventListener] history 저장 실패 - outbox FAILED 처리", e);
        } finally {
            outboxRepository.saveAll(outboxList);
        }
    }
}
