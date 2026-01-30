package org.example.javadata.controller;

import lombok.RequiredArgsConstructor;
import org.example.javadata.io.WeatherInput;
import org.example.javadata.service.WeatherService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class WeatherController {

    private final WeatherInput weatherInput;
    private final WeatherService weatherService;

    @GetMapping("/get/weather")
    public String test() throws Exception {
        return weatherInput.callApi(1, 10);
    }

    @GetMapping("/public")
    public String fetchAndSave(@RequestParam(defaultValue = "1") int pageNo,
                               @RequestParam(defaultValue = "10") int numOfRows) throws Exception {

        int saved = weatherService.fetchAndSave(pageNo, numOfRows);
        return "saved=" + saved;
    }

}
