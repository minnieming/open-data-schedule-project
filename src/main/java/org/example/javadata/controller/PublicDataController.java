package org.example.javadata.controller;

import lombok.RequiredArgsConstructor;
import org.example.javadata.io.AirQualityInput;
import org.example.javadata.io.TourismInput;
import org.example.javadata.service.AirQualityCollector;
import org.example.javadata.service.HolidayCollector;
import org.example.javadata.service.TourismVisitorCollector;
import org.example.javadata.service.WeatherWarnCollector;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PublicDataController {

    private final WeatherWarnCollector weatherWarnCollector;
    private final TourismVisitorCollector tourismVisitorCollector;
    private final HolidayCollector holidayCollector;
    private final AirQualityCollector airQualityCollector;
    private final TourismInput tourismInput;
    private final AirQualityInput airQualityInput;

    @GetMapping("/weather-warn")
    public String fetchWeatherWarn(@RequestParam(defaultValue = "1") int pageNo,
                                   @RequestParam(defaultValue = "10") int numOfRows) throws Exception {
        int saved = weatherWarnCollector.fetchAndSave(pageNo, numOfRows);
        return "saved=" + saved;
    }

    @GetMapping("/tourism")
    public String fetchTourism(@RequestParam(defaultValue = "1") int pageNo,
                               @RequestParam(defaultValue = "10") int numOfRows) throws Exception {
        int saved = tourismVisitorCollector.fetchAndSave(pageNo, numOfRows);
        return "saved=" + saved;
    }

    @GetMapping("/holiday")
    public String fetchHoliday(@RequestParam(defaultValue = "1") int pageNo,
                               @RequestParam(defaultValue = "100") int numOfRows) throws Exception {
        int saved = holidayCollector.fetchAndSave(pageNo, numOfRows);
        return "saved=" + saved;
    }

    @GetMapping("/air-quality")
    public String fetchAirQuality(@RequestParam(defaultValue = "1") int pageNo,
                                  @RequestParam(defaultValue = "100") int numOfRows) throws Exception {
        int saved = airQualityCollector.fetchAndSave(pageNo, numOfRows);
        return "saved=" + saved;
    }

    @GetMapping("/debug/tourism")
    public String debugTourism() throws Exception {
        return tourismInput.callApi(1, 10);
    }

    @GetMapping("/debug/air-quality")
    public String debugAirQuality() throws Exception {
        String body = airQualityInput.callApi(1, 10);
        return "URL=" + airQualityInput.getLastUri() + "\nBODY=" + body;
    }
}
