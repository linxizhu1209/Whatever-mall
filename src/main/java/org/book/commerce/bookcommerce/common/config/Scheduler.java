package org.book.commerce.bookcommerce.common.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.book.commerce.bookcommerce.domain.order.domain.Order;
import org.book.commerce.bookcommerce.domain.order.domain.OrderStatus;
import org.book.commerce.bookcommerce.domain.order.service.OrderService;
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
    @Transactional
    public void exceedDay(){
        log.info("스케쥴러 작동중: 현재시각 : "+ LocalDateTime.now());
        orderService.exceedOrderDay();
    }

}
