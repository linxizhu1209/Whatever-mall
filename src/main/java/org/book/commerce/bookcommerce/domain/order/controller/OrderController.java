package org.book.commerce.bookcommerce.domain.order.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.book.commerce.bookcommerce.domain.order.domain.Order;
import org.book.commerce.bookcommerce.domain.order.dto.OrderResultDto;
import org.book.commerce.bookcommerce.domain.order.dto.OrderlistDto;
import org.book.commerce.bookcommerce.domain.order.service.OrderService;
import org.book.commerce.bookcommerce.domain.user.domain.CustomUserDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@Tag(name="주문 API",description = "장바구니에 담긴 물품을 주문하고, 취소, 반품, 조회할 수 있는 API입니다")
@RequestMapping("/order")
public class OrderController {
    private final OrderService orderService;
    //todo 1.상품 주문하기 => 장바구니에 있는 상품 주문, 장바구니에 있는 상품 삭제
    @Operation(summary = "장바구니 물품 주문",description = "장바구니에 담긴 물품들을 주문한다(장바구니를 통해서만 주문 가능")
    @PostMapping("/pay")
    public ResponseEntity payOrder(@AuthenticationPrincipal CustomUserDetails customUserDetails){
        OrderResultDto orderResultDto = orderService.payOrder(customUserDetails);
        return ResponseEntity.status(HttpStatus.OK).body(orderResultDto);
    } // todo 주문했을때 재고가 없다면, 또는 재고가 한정적인데 여러명이 몰렸다면 동시성 이슈를 생각해봐야함

    //todo 2. 주문 조회하기 => 주문에 대한 상태를 조회 가능(주문 당일 주문완료 및 주문취소, 주문 후 +1일에 배송중, +2일에 배송완료)
    @Operation(summary = "나의 주문이력 조회",description = "나의 주문이력을 조회한다(주문번호,주문일자,주문상태 조회 가능")
    @GetMapping("/orderlist")
    public ResponseEntity<List<OrderlistDto>> orderList(@AuthenticationPrincipal CustomUserDetails customUserDetails){
        //todo 주문번호, 주문일자와 주문상태가 나타날건데,,,,, 여태 주문했던 내역을 모두 보여준다. 즉, 배송완료된 상품들까지,
        List<OrderlistDto> orderLists = orderService.getOrderList(customUserDetails);
        return ResponseEntity.status(HttpStatus.OK).body(orderLists);
    }
    // todo 3. 주문 취소하기 => 주문 취소시, 현재 상태가 주문완료인지 확인한다. 즉, 주문 당일인지 확인 => 취소 후 재고 복구, 상태는 취소완료
    @Operation(summary = "주문 취소",description = "주문한 물품을 주문 취소한다")
    @PutMapping("/cancel/{orderId}")
    public ResponseEntity cancelOrder(@PathVariable Long orderId){
        orderService.cancelOrder(orderId);
        return ResponseEntity.status(HttpStatus.OK).body("주문 취소가 완료되었습니다!");
    }
    // todo 4. 반품하기 => 현재 배송상태가 배송완료인지 확인, => 반품 신청상태로 바꾸고, 업데이트 된 날짜로부터 +1일 후에 재고에 반영 및 반품완료로 변경

    @Operation(summary = "물품 반품",description = "배송완료된 물품을 반품한다")
    @PutMapping("/refund/{orderId}")
    public ResponseEntity refundOrder(@PathVariable Long orderId){
        orderService.refundOrder(orderId);
        return ResponseEntity.status(HttpStatus.OK).body("반품 신청이 완료되었습니다!");
    }

}
