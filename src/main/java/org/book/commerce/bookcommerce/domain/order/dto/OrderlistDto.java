package org.book.commerce.bookcommerce.domain.order.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.book.commerce.bookcommerce.domain.order.domain.OrderStatus;

import java.util.Date;
import java.util.List;

// 나의 주문내역을 보여준다
@Builder
@Getter
@Setter
public class OrderlistDto {

    private Long orderId;
    private Date orderDate; // 주문 날짜
    private OrderStatus orderStatus;

    private List<OrderProductListDto> orderProductList; // 주문한 아이템 목록

}
