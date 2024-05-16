package org.book.commerce.orderservice.controller;


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
@RequestMapping("/order")
public class OrderController {
    private final OrderService orderService;

    @PostMapping("/orderCartList")
    public ResponseEntity<OrderResultDto> orderCartList(@RequestHeader("X-Authorization-Id") String userEmail){
        OrderResultDto orderResultDto = orderService.orderCartList(userEmail);
        return ResponseEntity.status(HttpStatus.OK).body(orderResultDto);
    }

    @PostMapping("/orderProduct")
    public ResponseEntity<OrderResultDto> orderProduct(@RequestHeader("X-Authorization-Id") String userEmail,
                                          @RequestBody ReqBuyProduct reqBuyProduct){
            OrderResultDto orderResultDto = orderService.orderProduct(userEmail,reqBuyProduct);
            return ResponseEntity.status(HttpStatus.OK).body(orderResultDto);
    }

    @PutMapping("/payOrder/{orderId}")
    public ResponseEntity<String> payOrder(@PathVariable Long orderId,
                                   @RequestBody PayInfo payInfo){
            log.info("[OrderService] 주문 결제 요청이 들어왔습니다. 주문번호 = "+orderId);
            boolean isPaid = orderService.payOrder(orderId,payInfo);
            if(isPaid) return ResponseEntity.status(HttpStatus.OK).body("주문한 건에 대해 결제가 완료되었습니다!");
            else return ResponseEntity.status(HttpStatus.OK).body("주문한 건에 대하여 정상적으로 결제 취소되었습니다.");
    }

    @GetMapping("/orderlist")
    public ResponseEntity<List<OrderlistDto>> orderList(@RequestHeader("X-Authorization-Id") String userEmail){
        List<OrderlistDto> orderLists = orderService.getOrderList(userEmail);
        return ResponseEntity.status(HttpStatus.OK).body(orderLists);
    }

    @PutMapping("/cancel/{orderId}")
    public ResponseEntity<String> cancelOrder(@PathVariable Long orderId){
        orderService.cancelOrder(orderId);
        return ResponseEntity.status(HttpStatus.OK).body("주문 취소가 완료되었습니다!");
    }

    @PutMapping("/refund/{orderId}")
    public ResponseEntity<String> refundOrder(@PathVariable Long orderId){
        orderService.refundOrder(orderId);
        return ResponseEntity.status(HttpStatus.OK).body("반품 신청이 완료되었습니다!");
    }

}
