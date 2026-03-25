package org.example.javadata.controller;

import lombok.RequiredArgsConstructor;
import org.example.javadata.entity.WeatherLatestEntity;
import org.example.javadata.io.WeatherInput;
import org.example.javadata.repository.WeatherLatestRepository;
import org.example.javadata.service.WeatherCollector;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class WeatherController {

    private final WeatherInput weatherInput;
    private final WeatherCollector weatherCollector;
    private final WeatherLatestRepository weatherLatestRepository;

    @GetMapping("/get/weather")
    public String test() throws Exception {
        return weatherInput.callApi(1, 10);
    }

    @GetMapping("/public")
    public String fetchAndSave(@RequestParam(defaultValue = "1") int pageNo,
                               @RequestParam(defaultValue = "10") int numOfRows) throws Exception {
        int saved = weatherCollector.fetchAndSave(pageNo, numOfRows);
        return "saved=" + saved;
    }

    @GetMapping("/weather/latest")
    public List<WeatherLatestEntity> getLatest() {
        return weatherLatestRepository.findAll();
    }
}
