package org.book.commerce.productservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class StockScheduler {
    private final ProductService productService;

    @Scheduled(cron = "0 */30 * * * *")
    public void synchronizeDB(){
        log.info("[Synchronize DB-Cache] 스케줄러 작동중: 현재시각 : "+ LocalDateTime.now());
        productService.synchronizeDB();
    }
}
