package org.example.javadata.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HttpTest {
    @GetMapping("/ping")
    public String ping() {
        System.out.println("http 연결 테스트");
        return "pong";
    }
}

