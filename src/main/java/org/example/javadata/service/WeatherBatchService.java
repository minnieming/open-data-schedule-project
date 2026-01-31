package org.example.javadata.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class WeatherBatchService {

    private final WeatherService weatherService;

    // 단일호출
    public void run() {
        try {
            weatherService.fetchAndSave(1, 100);
            log.info("Weather batch 실행 완료");
        } catch (Exception e) {
            log.error("Weather batch 실행 실패", e);
        }
    }


}