package org.example.javadata.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 공공데이터 수집 배치 서비스
 *
 * 핵심:
 * - 스프링이 자동으로 모든 PublicDataCollector 구현체를 찾아서 리스트에 주입
 * - 새로운 Collector 추가? → 자동으로 추가됨 (코드 수정 없음!)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PublicDataBatchService {

    // 모든 PublicDataCollector 구현체가 자동으로 주입됨
    private final List<PublicDataCollector> collectors;

    /**
     * 모든 공공데이터를 수집한다
     */
    public void run() {
        log.info("========== 공공데이터 수집 시작 ==========");
        long startTime = System.currentTimeMillis();

        int totalCollected = 0;
        int failureCount = 0;

        for (PublicDataCollector collector : collectors) {
            try {
                log.info("[{}] 수집 시작...", collector.getCollectorName());

                int count = collector.collect();

                log.info("[{}] 수집 완료: {} 개", collector.getCollectorName(), count);
                totalCollected += count;
            } catch (Exception e) {
                log.error("[{}] 수집 실패", collector.getCollectorName(), e);
                failureCount++;
            }
        }

        long endTime = System.currentTimeMillis();
        log.info("========== 공공데이터 수집 완료 ==========");
        log.info("총 수집: {} 개, 실패: {} 개, 소요시간: {}ms",
                totalCollected, failureCount, endTime - startTime);
    }
}
