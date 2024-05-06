package org.book.commerce.orderservice.config;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.book.commerce.orderservice.service.OrderService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class Scheduler {
    private final OrderService orderService;

    @Scheduled(cron="0 */30 * * * *") // 30분에 한번씩
    public void exceedDay(){
        log.info("[Confirm OrderStatus] 스케쥴러 작동중: 현재시각 : "+ LocalDateTime.now());
        orderService.exceedOrderDay();
    }

    @Scheduled(cron="0 */1 * * * *") // 1분에 한번씩
    public void overPaymentDeadLine(){
        log.info("[Confirm PaymentDeadline] 스케쥴러 작동중: 현재시각 : "+ LocalDateTime.now());
        orderService.overPaymentDeadLine();
    }


    // 비동기 처리 방식으로 해도됨.
    // 상태별로 가져오는 방식으로 해보자. 날짜도 같이 비교해서 가져오는 방법?
}