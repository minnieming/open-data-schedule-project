package org.example.javadata.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.javadata.entity.WeatherHistoryEntity;
import org.example.javadata.repository.WeatherHistoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeleteBenchmarkService {

    private final WeatherHistoryRepository weatherHistoryRepository;

    // 더미 데이터 삽입
    @Transactional
    public int insertDummy(int count) {
        List<WeatherHistoryEntity> entities = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            entities.add(WeatherHistoryEntity.builder()
                    .regionCode("TEST_" + (i % 100))
                    .regionName("테스트지역" + (i % 100))
                    .skyStatus("맑음")
                    .observedTime("2024010100")
                    .forecastTime("2024010100")
                    .rainType("없음")
                    .currentTemp(20.0)
                    .humidity(50)
                    .rainProbability(10)
                    .rainAmount1h(0.0)
                    .tempAfter3h(21.0)
                    .xCoord(60)
                    .yCoord(127)
                    .build());
        }
        weatherHistoryRepository.saveAll(entities);
        log.info("[Benchmark] 더미 데이터 {} 건 삽입 완료", count);
        return count;
    }

    // Before: ID 목록 조회 후 1건씩 개별 삭제
    @Transactional
    public long slowDelete() {
        List<Long> ids = weatherHistoryRepository.findAllIds();
        log.info("[Benchmark][SLOW] 삭제 대상: {} 건, 시작", ids.size());

        long start = System.currentTimeMillis();
        for (Long id : ids) {
            weatherHistoryRepository.deleteById(id);
        }
        long elapsed = System.currentTimeMillis() - start;

        log.info("[Benchmark][SLOW] 삭제 완료 - {} 건 / {} ms", ids.size(), elapsed);
        return elapsed;
    }

    // After: PK 범위 청크 분할 + 멀티스레드 병렬 삭제
    public long fastDelete(int threadCount) throws Exception {
        Long minId = weatherHistoryRepository.findMinId();
        Long maxId = weatherHistoryRepository.findMaxId();

        if (minId == null || maxId == null) {
            log.info("[Benchmark][FAST] 삭제할 데이터 없음");
            return 0;
        }

        long total = maxId - minId + 1;
        long chunkSize = (long) Math.ceil((double) total / threadCount);

        log.info("[Benchmark][FAST] 삭제 시작 - id: {} ~ {}, 스레드: {}개, 청크 크기: {}",
                minId, maxId, threadCount, chunkSize);

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        List<Future<?>> futures = new ArrayList<>();

        long start = System.currentTimeMillis();

        for (int i = 0; i < threadCount; i++) {
            long startId = minId + (chunkSize * i);
            long endId = Math.min(startId + chunkSize - 1, maxId);
            int threadNum = i + 1;

            futures.add(executor.submit(() -> {
                log.info("[Benchmark][FAST] Thread-{}: DELETE id {} ~ {}", threadNum, startId, endId);
                weatherHistoryRepository.deleteByIdBetween(startId, endId);
                log.info("[Benchmark][FAST] Thread-{}: 완료", threadNum);
            }));
        }

        for (Future<?> f : futures) {
            f.get();
        }
        executor.shutdown();

        long elapsed = System.currentTimeMillis() - start;
        log.info("[Benchmark][FAST] 삭제 완료 - {} ms", elapsed);
        return elapsed;
    }
}
