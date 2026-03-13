package org.example.javadata.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.javadata.entity.WeatherHistoryEntity;
import org.example.javadata.repository.WeatherHistoryRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class InsertBenchmarkService {

    private final WeatherHistoryRepository weatherHistoryRepository;
    private final JdbcTemplate jdbcTemplate;

    private List<WeatherHistoryEntity> buildEntities(int count) {
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
        return entities;
    }

    // Before: JPA saveAll - 1건씩 개별 INSERT
    @Transactional
    public long slowInsert(int count) {
        List<WeatherHistoryEntity> entities = buildEntities(count);
        log.info("[Benchmark][SLOW] INSERT 시작 - {} 건", count);

        long start = System.currentTimeMillis();
        weatherHistoryRepository.saveAll(entities);
        long elapsed = System.currentTimeMillis() - start;

        log.info("[Benchmark][SLOW] INSERT 완료 - {} 건 / {} ms", count, elapsed);
        return elapsed;
    }

    // After: JdbcTemplate batchUpdate - 한 번에 묶어서 INSERT
    @Transactional
    public long fastInsert(int count) {
        List<WeatherHistoryEntity> entities = buildEntities(count);
        log.info("[Benchmark][FAST] BATCH INSERT 시작 - {} 건", count);

        String sql = "INSERT INTO weather_history " +
                "(region_code, region_name, sky_status, observed_time, forecast_time, rain_type, " +
                "current_temp, humidity, rain_probability, rain_amount1h, temp_after3h, x_coord, y_coord) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        long start = System.currentTimeMillis();
        jdbcTemplate.batchUpdate(sql, entities, entities.size(), (ps, e) -> {
            ps.setString(1, e.getRegionCode());
            ps.setString(2, e.getRegionName());
            ps.setString(3, e.getSkyStatus());
            ps.setString(4, e.getObservedTime());
            ps.setString(5, e.getForecastTime());
            ps.setString(6, e.getRainType());
            ps.setDouble(7, e.getCurrentTemp());
            ps.setInt(8, e.getHumidity());
            ps.setInt(9, e.getRainProbability());
            ps.setDouble(10, e.getRainAmount1h());
            ps.setDouble(11, e.getTempAfter3h());
            ps.setInt(12, e.getXCoord());
            ps.setInt(13, e.getYCoord());
        });
        long elapsed = System.currentTimeMillis() - start;

        log.info("[Benchmark][FAST] BATCH INSERT 완료 - {} 건 / {} ms", count, elapsed);
        return elapsed;
    }
}
