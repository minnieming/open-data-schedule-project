package org.example.javadata.controller;

import lombok.RequiredArgsConstructor;
import org.example.javadata.service.DeleteBenchmarkService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class DeleteBenchmarkController {

    private final DeleteBenchmarkService deleteBenchmarkService;

    // 1. 더미 데이터 삽입
    @PostMapping("/insert")
    public String insert(@RequestParam(defaultValue = "100000") int count) throws Exception {
        int inserted = deleteBenchmarkService.insertDummy(count);
        return "inserted=" + inserted;
    }

    // 2. Before: 1건씩 개별 삭제
    @DeleteMapping("/delete/slow")
    public String slowDelete() throws Exception {
        long elapsed = deleteBenchmarkService.slowDelete();
        return "slow delete elapsed=" + elapsed + "ms";
    }

    // 3. After: 멀티스레드 청크 삭제
    @DeleteMapping("/delete/fast")
    public String fastDelete(@RequestParam(defaultValue = "5") int threads) throws Exception {
        long elapsed = deleteBenchmarkService.fastDelete(threads);
        return "fast delete (threads=" + threads + ") elapsed=" + elapsed + "ms";
    }
}
