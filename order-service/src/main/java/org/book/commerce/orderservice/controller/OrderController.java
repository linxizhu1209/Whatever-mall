package org.book.commerce.orderservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.book.commerce.orderservice.dto.ReqBuyProduct;
import org.book.commerce.orderservice.dto.OrderResultDto;
import org.book.commerce.orderservice.dto.OrderlistDto;
import org.book.commerce.orderservice.dto.PayInfo;
import org.book.commerce.orderservice.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@Slf4j
@Tag(name="주문 API",description = "장바구니에 담긴 물품을 주문하고, 취소, 반품, 조회할 수 있는 API입니다")
@RequestMapping("/order")
public class OrderController {
    private final OrderService orderService;
    //todo 1.상품 주문하기 => 장바구니에 있는 상품 주문, 장바구니에 있는 상품 삭제
    @Operation(summary = "장바구니 물품 주문", description = "장바구니에서 바로 주문을 한다")
    @PostMapping("/orderCartList")
    public ResponseEntity<OrderResultDto> orderCartList(@RequestHeader("X-Authorization-Id") String userEmail){
        OrderResultDto orderResultDto = orderService.orderCartList(userEmail);
        return ResponseEntity.status(HttpStatus.OK).body(orderResultDto);
    }

    @Operation(summary = "상세페이지 물품 주문", description = "상세페이지에서 바로 주문을 한다")
    @PostMapping("/orderProduct")
    public ResponseEntity<OrderResultDto> orderProduct(@RequestHeader("X-Authorization-Id") String userEmail,
                                          @RequestBody ReqBuyProduct reqBuyProduct){
            OrderResultDto orderResultDto = orderService.orderProduct(userEmail,reqBuyProduct);
            return ResponseEntity.status(HttpStatus.OK).body(orderResultDto);
    }

    @Operation(summary = "주문 건 결제 요청", description = "주문한 제품을 결제한다")
    @PutMapping("/payOrder/{orderId}")
    public ResponseEntity<String> payOrder(@PathVariable Long orderId,
                                   @RequestBody PayInfo payInfo){
            log.info("[OrderService] 주문 결제 요청이 들어왔습니다. 주문번호 = "+orderId);
            orderService.payOrder(orderId,payInfo);
            return ResponseEntity.status(HttpStatus.OK).body("주문한 건에 대해 결제가 완료되었습니다!");
    }


    @Operation(summary = "나의 주문이력 조회",description = "나의 주문이력을 조회한다(주문번호,주문일자,주문상태 조회 가능")
    @GetMapping("/orderlist")
    public ResponseEntity<List<OrderlistDto>> orderList(@RequestHeader("X-Authorization-Id") String userEmail){
        //todo 주문번호, 주문일자와 주문상태가 나타날건데,,,,, 여태 주문했던 내역을 모두 보여준다. 즉, 배송완료된 상품들까지,
        // todo 최신순으로 보여주기!
        List<OrderlistDto> orderLists = orderService.getOrderList(userEmail);
        return ResponseEntity.status(HttpStatus.OK).body(orderLists);
    }
    @Operation(summary = "주문 취소",description = "주문한 물품을 주문 취소한다")
    @PutMapping("/cancel/{orderId}")
    public ResponseEntity<String> cancelOrder(@PathVariable Long orderId){
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
