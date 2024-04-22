package org.book.commerce.bookcommerce.domain.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.book.commerce.bookcommerce.domain.order.domain.OrderStatus;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

// 나의 주문내역을 보여준다
@Builder
@Getter
@Setter
public class OrderlistDto {
    @Schema(description = "주문 고유번호",example = "1")
    private Long orderId;
    @Schema(description = "주문한 날짜",example = "2024-04-22")
    private LocalDateTime orderDate;
    @Schema(description = "주문상태",example = "배송완료")
    private OrderStatus orderStatus;
    @Schema(description = "주문한 상품 목록",example = "주문한 상품 목록")
    private List<OrderProductListDto> orderProductList; // 주문한 아이템 목록

}
