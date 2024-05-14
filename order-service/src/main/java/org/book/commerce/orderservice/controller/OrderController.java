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
            boolean isPaid = orderService.payOrder(orderId,payInfo);
            if(isPaid) return ResponseEntity.status(HttpStatus.OK).body("주문한 건에 대해 결제가 완료되었습니다!");
            else return ResponseEntity.status(HttpStatus.OK).body("주문한 건에 대하여 정상적으로 결제 취소되었습니다.");
    }


    @Operation(summary = "나의 주문이력 조회",description = "나의 주문이력을 조회한다(주문번호,주문일자,주문상태 조회 가능")
    @GetMapping("/orderlist")
    public ResponseEntity<List<OrderlistDto>> orderList(@RequestHeader("X-Authorization-Id") String userEmail){
        List<OrderlistDto> orderLists = orderService.getOrderList(userEmail);
        return ResponseEntity.status(HttpStatus.OK).body(orderLists);
    }
    @Operation(summary = "주문 취소",description = "주문한 물품을 주문 취소한다(배송 상태가 \"주문완료\"인 경우에만 가능")
    @PutMapping("/cancel/{orderId}")
    public ResponseEntity<String> cancelOrder(@PathVariable Long orderId){
        orderService.cancelOrder(orderId);
        return ResponseEntity.status(HttpStatus.OK).body("주문 취소가 완료되었습니다!");
    }

    @Operation(summary = "물품 반품",description = "배송완료된 물품을 반품한다")
    @PutMapping("/refund/{orderId}")
    public ResponseEntity<String> refundOrder(@PathVariable Long orderId){
        orderService.refundOrder(orderId);
        return ResponseEntity.status(HttpStatus.OK).body("반품 신청이 완료되었습니다!");
    }

}
