package org.example.javadata.controller;

import lombok.RequiredArgsConstructor;
import org.example.javadata.io.WeatherInput;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class WeatherController {

    private final WeatherInput weatherInput;

    @GetMapping("/get/weather")
    public String test() throws Exception {
        return weatherInput.callApi(1, 10);
    }

}
