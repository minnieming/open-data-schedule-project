package org.example.javadata.controller;

import lombok.RequiredArgsConstructor;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.example.javadata.service.PublicDataBatchService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SchedulerController {

    private final PublicDataBatchService batchService;

    @Scheduled(cron = "0 */5 * * * *")
    @SchedulerLock(
            name = "publicDataFetchJob",
            lockAtLeastFor = "PT1M",
            lockAtMostFor = "PT10M"
    )
    public void run() {
        batchService.run();
    }
}