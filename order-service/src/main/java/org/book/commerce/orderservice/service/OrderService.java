package org.book.commerce.orderservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.book.commerce.orderservice.dto.ReqBuyProduct;
import org.book.commerce.common.entity.ErrorCode;
import org.book.commerce.common.exception.CommonException;
import org.book.commerce.common.exception.ConflictException;
import org.book.commerce.common.exception.NotAcceptException;
import org.book.commerce.common.exception.NotFoundException;
import org.book.commerce.common.security.CustomUserDetails;
import org.book.commerce.orderservice.domain.*;
import org.book.commerce.orderservice.dto.*;
import org.book.commerce.orderservice.repository.OrderRepository;
import org.book.commerce.orderservice.repository.ProductOrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final ProductOrderRepository productOrderRepository;
    private final CartOrderFeignClient cartOrderFeignClient;
    private final OrderProductFeignClient orderProductFeignClient;

    public List<OrderlistDto> getOrderList(CustomUserDetails customUserDetails) {
        // todo "결제대기"중인 주문건은 주문내역에 잡히지 말아야함
        String userId = customUserDetails.getUsername();
        List<Order> orderList = orderRepository.findAllByUserEmail(userId);
        ArrayList<OrderlistDto> orderlistDtos = new ArrayList<>();
        for(Order order:orderList){ // 사용자의 여태까지의 모든 주문내역을 조회하고 그 주문내역하나당 물품을 다 보여줘야함
            List<ProductOrder> productOrderList = productOrderRepository.findAllByOrderId(order.getOrderId());
            long[] productIdArr = productOrderList.stream().map(ProductOrder::getProductId).mapToLong(i->i).toArray();
            // product,productprice,productId를 가져와야함
            List<ProductFeignResponse> productFeignlist = orderProductFeignClient.findProductByProductId(productIdArr);
            List<OrderProductListDto> orderProductListDtos = new ArrayList<>();
            for(ProductFeignResponse product:productFeignlist){
                int orderCount = productOrderRepository.findByProductIdAndOrderId(product.productId(), order.getOrderId()).getCount();
                orderProductListDtos.add(OrderProductListDto.builder().productName(product.name())
                        .price(product.price()).productId(product.productId()).count(orderCount).build());
            }
            orderlistDtos.add(OrderlistDto.builder().orderId(order.getOrderId())
                    .orderStatus(order.getStatus()).orderDate(order.getCreatedAt())
                    .orderProductList(orderProductListDtos).build());
        }
        // 이중 for문을 없애는 방법과 호출 줄이는 방법 없을지 고민
        // orderlist에는 간단하게 주문id와 주문날짜, 상태만 보여주고 상세로 들어가야지 주문 아이템 목록을 보여주는 것도 생각
        return orderlistDtos;
    }

    public void cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(()->new NotFoundException("요청한 주문을 찾을 수 없습니다. 문제 주문 번호: "+orderId));
        if(order.getStatus()==OrderStatus.ORDER_COMPLETE){
            order.setStatus(OrderStatus.REQ_CANCEL);
            orderRepository.save(order);
        }
        else{
            throw new CommonException("배송중인 상품으로 주문 취소가 불가능합니다.", ErrorCode.BAD_REQUEST);
        }
    }

    public void refundOrder(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(()->new NotFoundException("존재하지 않는 주문입니다."));
        if(order.getStatus()==OrderStatus.FINISH_SHIPPING){
            order.setStatus(OrderStatus.REQ_REFUND);
            orderRepository.save(order);
        }
        else{
            throw new NotAcceptException("반품이 불가능한 상태입니다.");
        }
    }

    @Transactional
    public void exceedOrderDay() {
        List<Order> orderlist = orderRepository.findAllOlderThanLast24HoursWithSpecificStatus(OrderStatus.ORDER_COMPLETE,OrderStatus.REQ_CANCEL,OrderStatus.SHIPPING,OrderStatus.REQ_REFUND);

        // 모든 주문에 대한 상태 업데이트 및 처리
        for (Order order : orderlist) {
            OrderStatus currentStatus = order.getStatus();
            if (currentStatus == OrderStatus.ORDER_COMPLETE ) {
                order.setStatus(OrderStatus.SHIPPING);
            } else if (currentStatus == OrderStatus.SHIPPING) {
                order.setStatus(OrderStatus.FINISH_SHIPPING);
            } else if (currentStatus == OrderStatus.REQ_REFUND) {
                order.setStatus(OrderStatus.FINISH_REFUND);
                returnStock(order);
            } else if (currentStatus == OrderStatus.REQ_CANCEL) {
                order.setStatus(OrderStatus.ORDER_CANCEL);
                returnStock(order);
            }
            orderRepository.save(order);
        }
    }

    public OrderResultDto orderCartList(CustomUserDetails customUserDetails) {
        String userEmail = customUserDetails.getUsername();
        Order order = Order.builder().userEmail(userEmail)
                .status(OrderStatus.WAITING_PAYING)
                .build();
        Long orderId = orderRepository.save(order).getOrderId();
        List<CartOrderFeignResponse> cartList = cartOrderFeignClient.findCartListByUserEmail(userEmail);
        ArrayList<OrderProductCountFeignRequest> orderProductCountList = new ArrayList<>();
        for(CartOrderFeignResponse cart:cartList) {
            orderProductCountList.add(new OrderProductCountFeignRequest(cart.productId(), cart.count()));
        }
        orderProductFeignClient.minusStockList(orderProductCountList); // 재고 감소 로직

        ArrayList<ProductOrder> productOrders = new ArrayList<>();
        for(CartOrderFeignResponse cart:cartList) {
            ProductOrder productOrder = ProductOrder.builder().productId(cart.productId())
                    .orderId(orderId).count(cart.count()).build();
            productOrders.add(productOrder);
        };
        productOrderRepository.saveAll(productOrders);
        cartOrderFeignClient.deleteAllCart(userEmail);
        // 위에는 userEmail 넘기면 그 유저의 장바구니는 다 지워버리도록(장바구니에서 주문이 가능하고, 한번주문할때 장바구니에 들어있는 모든 물품을 주문하는것이므로)

        return new OrderResultDto(orderId);
    }

    @Transactional
    public OrderResultDto orderProduct(CustomUserDetails customUserDetails, ReqBuyProduct reqBuyProduct) {
        orderProductFeignClient.minusStock(new OrderProductCountFeignRequest(reqBuyProduct.getProductId(), reqBuyProduct.getQuantity()));
        log.info("재고가 성공적으로 감소하였습니다.");
        Order order = Order.builder().status(OrderStatus.WAITING_PAYING).userEmail(customUserDetails.getUsername()).build();
        Long orderId = orderRepository.save(order).getOrderId();
        ProductOrder productOrder = ProductOrder.builder().productId(reqBuyProduct.getProductId()).count(reqBuyProduct.getQuantity())
                .orderId(orderId).build();
        productOrderRepository.save(productOrder);
        return new OrderResultDto(orderId);
    }
    @Transactional
    public void payOrder(Long orderId, PayInfo payInfo) {
        if(payInfo.getIsCanceled()){
            //todo 재고 돌려놓기 ==> 주문 시에 주문 내역테이블을 만드는게 나은가? 그럼만약 결제취소라면 주문내역테이블을 제거?
            Order order = findOrderById(orderId);
            order.setStatus(OrderStatus.ORDER_CANCEL);
            returnStock(order);
        }
        else{ // 결제 진행
            if(payInfo.getIsLimitExcess()){ //한도 초과인 경우(고객 귀책)
                Order order = findOrderById(orderId);
                order.setStatus(OrderStatus.ORDER_CANCEL);
                returnStock(order);
                orderRepository.save(order);
                throw new ConflictException("한도초과로 결제가 불가능합니다. 주문이 취소되었습니다.");
            }
            else{ //한도초과도 아니고 정상적으로 결제된 경우
                Order order = findOrderById(orderId);
                order.setStatus(OrderStatus.ORDER_COMPLETE);
            }
        }
    }

    private Order findOrderById(Long orderId){
        return orderRepository.findById(orderId).orElseThrow(()->new NotFoundException("존재하지 않는 주문입니다."));
    }

    private void returnStock(Order order){
        List<ProductOrder> productOrderList = productOrderRepository.findAllByOrderId(order.getOrderId());
        ArrayList<OrderProductCountFeignRequest> orderProductCountList = new ArrayList<>();
        for(ProductOrder productOrder:productOrderList) {
            orderProductCountList.add(new OrderProductCountFeignRequest(productOrder.getProductId(), productOrder.getCount()));
        };
        // todo count를 넘겨주면 재고 증가시키도록 (plusStock) => 재고 증가시키고 물품 저장까지
        orderProductFeignClient.plusStock(orderProductCountList);
        log.info("재고가 성공적으로 변경되었습니다.");
    }

    @Transactional
    public void overPaymentDeadLine() {
        List<Order> orderList = orderRepository.findAllOlderThan10MinWithWatingPayingStatus(OrderStatus.WAITING_PAYING);
        for (Order order : orderList) {
            order.setStatus(OrderStatus.ORDER_CANCEL);
            returnStock(order);
        // todo 한꺼번에 상태를 바꾸고, 한꺼번에 넘겨서 재고 변동하도록 로직 바꿔보기
        }
    }



//    public void reqPayment(ReqPaymentDto reqPaymentDto, Long paymentId) {
//        Payment payment = paymentRepository.findById(paymentId).orElseThrow(()->new NotFoundException("해당하는 결제요청건을 찾을 수 없습니다 paymentID: "+paymentId));
//        payment.setPaymentStatus(PaymentStatus.IN_PAYMENT);
//        payment.setDeliveryAddress(reqPaymentDto.getDeliveryAddress());
//        payment.setRecipient(reqPaymentDto.getRecipient());
//        payment.setRecipientPhone(reqPaymentDto.getPhoneNum());
//        paymentRepository.save(payment); // todo 요청dto에서 엔티티에 값을 넣어줄때 값을빼먹지않게 설정해주는 방법은 없을까
//    }
//    //todo  만약 결제창, 결제중, 결제완료 중 하나라도 오류가 발생한다면, 재고 회복 메서드가 실행되도록
//
//    public OrderResultDto finishPayment(Long paymentId) {
//        Payment payment = paymentRepository.findById(paymentId).orElseThrow(()->new NotFoundException("해당하는 결제요청건을 찾을 수 없습니다 paymentID: "+paymentId));
//        payment.setPaymentStatus(PaymentStatus.FIN_PAYMENT);
//        paymentRepository.save(payment);
//        return completeOrder(payment);
//    }

//    private OrderResultDto completeOrder(Payment payment){
//        Order order = Order.builder().userEmail(payment.getUserEmail())
//                .paymentId(payment.getPaymentId()).status(OrderStatus.ORDER_COMPLETE)
//                .build();
//        Long orderId = orderRepository.save(order).getOrderId();
//        if(payment.getIsCartList()){
//            // 장바구니에서 주문하기로 넘어온 경우
//            List<CartOrderFeignResponse> cartList = cartOrderFeignClient.findCartListByUserEmail(payment.getUserEmail());
//
//            ArrayList<ProductOrder> productOrders = new ArrayList<>();
//            for(CartOrderFeignResponse cart:cartList) {
//                ProductOrder productOrder = ProductOrder.builder().productId(cart.productId())
//                        .orderId(orderId).count(cart.count()).build();
//                productOrders.add(productOrder);
//            };
//            productOrderRepository.saveAll(productOrders);
//            // 위에까지는 for문으로 돌면됨 cartList를 돌면서 생성 방식. 그치만 save를 한번에 하도록 list에 추가하는 식으로 하기
//            cartOrderFeignClient.deleteAllCart(payment.getUserEmail());
//            // 위에는 userEmail 넘기면 그 유저의 장바구니는 다 지워버리도록(장바구니에서 주문이 가능하고, 한번주문할때 장바구니에 들어있는 모든 물품을 주문하는것이므로)
//        }
//        else{
//            // 바로주문하기로 온 경우
//            ProductOrder productOrder = ProductOrder.builder().productId(payment.getProductId()).orderId(orderId)
//                    .count(payment.getCount()).build();
//            productOrderRepository.save(productOrder);
//        }
//        return new OrderResultDto(orderId);
//
//    }
}
