package org.book.commerce.orderservice.config;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.book.commerce.orderservice.service.OrderService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class Scheduler {
    private final OrderService orderService;

    @Scheduled(cron="0 */30 * * * *")
    public void exceedDay(){
        log.info("[Confirm OrderStatus] 스케쥴러 작동중: 현재시각 : "+ LocalDateTime.now());
        orderService.exceedOrderDay();
    }

    @Scheduled(cron="0 */1 * * * *")
    public void overPaymentDeadLine(){
        log.info("[Confirm PaymentDeadline] 스케쥴러 작동중: 현재시각 : "+ LocalDateTime.now());
        orderService.overPaymentDeadLine();
    }
}