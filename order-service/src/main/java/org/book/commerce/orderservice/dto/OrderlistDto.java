package org.book.commerce.orderservice.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.book.commerce.orderservice.domain.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
@Setter
public class OrderlistDto {

    private Long orderId;
    private LocalDateTime orderDate;
    private OrderStatus orderStatus;
    private List<OrderProductListDto> orderProductList; // 주문한 아이템 목록

}
