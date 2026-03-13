package org.example.javadata.controller;

import lombok.RequiredArgsConstructor;
import org.example.javadata.service.InsertBenchmarkService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/test/insert")
@RequiredArgsConstructor
public class InsertBenchmarkController {

    private final InsertBenchmarkService insertBenchmarkService;

    // Before: JPA saveAll
    @PostMapping("/slow")
    public String slowInsert(@RequestParam(defaultValue = "100000") int count) throws Exception {
        long elapsed = insertBenchmarkService.slowInsert(count);
        return "slow insert (count=" + count + ") elapsed=" + elapsed + "ms";
    }

    // After: JdbcTemplate batchUpdate
    @PostMapping("/fast")
    public String fastInsert(@RequestParam(defaultValue = "100000") int count) throws Exception {
        long elapsed = insertBenchmarkService.fastInsert(count);
        return "fast insert (count=" + count + ") elapsed=" + elapsed + "ms";
    }
}
