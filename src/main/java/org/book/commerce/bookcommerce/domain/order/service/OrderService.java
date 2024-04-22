package org.book.commerce.bookcommerce.domain.order.service;

import lombok.RequiredArgsConstructor;
import org.antlr.v4.runtime.atn.SemanticContext;
import org.aspectj.weaver.ast.Or;
import org.book.commerce.bookcommerce.domain.cart.domain.Cart;
import org.book.commerce.bookcommerce.domain.cart.domain.CartStatus;
import org.book.commerce.bookcommerce.domain.cart.repository.CartRepository;
import org.book.commerce.bookcommerce.domain.order.domain.Order;
import org.book.commerce.bookcommerce.domain.order.domain.OrderStatus;
import org.book.commerce.bookcommerce.domain.order.dto.OrderProductListDto;
import org.book.commerce.bookcommerce.domain.order.dto.OrderResultDto;
import org.book.commerce.bookcommerce.domain.order.dto.OrderlistDto;
import org.book.commerce.bookcommerce.domain.order.repository.OrderRepository;
import org.book.commerce.bookcommerce.domain.product.domain.Product;
import org.book.commerce.bookcommerce.domain.product.repository.ProductRepository;
import org.book.commerce.bookcommerce.domain.user.domain.CustomUserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    public OrderResultDto payOrder(CustomUserDetails customUserDetails) {
        String userId = customUserDetails.getUsername();
        List<Cart> cartList = cartRepository.findAllByUserEmailAndStatus(userId, CartStatus.ORDER_INCOMPLETE); // 장바구니 삭제보다는 주문완료로 상태변경, 그리고 주문 테이블에는 주문완료로
        Order order = Order.builder().userEmail(userId).status(OrderStatus.ORDER_COMPLETE).build();
        Long orderId = orderRepository.save(order).getOrderId();
        for(Cart cart:cartList){
            cart.setStatus(CartStatus.ORDER_COMPLETE);
            cart.setOrderId(orderId); // 주문 아이디로 장바구니에서 주문목록을 조회하기위함
            cartRepository.save(cart);
            int count = cart.getCount();
            Product product = productRepository.findById(cart.getProductId()).orElseThrow();
            int nowStock = product.getStock()-count;
            if(nowStock<0) throw new RuntimeException("주문하신 상품의 재고가 부족하여 구매를 할 수 없습니다. 확인해주세요. 상품 번호:"+product.getProductId());
            product.setStock(product.getStock()-count);
            productRepository.save(product);
        }
        return new OrderResultDto(orderId);
    }


    public List<OrderlistDto> getOrderList(CustomUserDetails customUserDetails) {
        String userId = customUserDetails.getUsername();
        List<Order> orderList = orderRepository.findAllByUserEmail(userId);
        ArrayList<OrderlistDto> orderlistDtos = new ArrayList<>();
        for(Order order:orderList){
            List<Cart> cartList = cartRepository.findAllByOrderId(order.getOrderId());
            List<OrderProductListDto> orderProductListDtos = new ArrayList<>();
            for(Cart cart:cartList){
                Product product = productRepository.findById(cart.getProductId()).orElseThrow();
                orderProductListDtos.add(OrderProductListDto.builder().productName(product.getName())
                        .price(product.getPrice()).count(cart.getCount()).build());
            }
            orderlistDtos.add(OrderlistDto.builder().orderId(order.getOrderId())
                    .orderStatus(order.getStatus()).orderDate(order.getCreatedAt())
                    .orderProductList(orderProductListDtos).build());
        } // 이중 for문이라 좋지않음. 그리고 장바구니 - 물품으로 계속 호출해야해서 msa적용하기 쉽지않을거같음
        // 이중 for문을 없애고 호출을 줄이는 방법을 연구해봐야함
        // orderlist에는 간단하게 주문id와 주문날짜, 상태만 보여주고 상세로 들어가야지 주문 아이템 목록을 보여주는 것도 생각
        return orderlistDtos;
    }

    public void cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow();
        if(order.getStatus()==OrderStatus.ORDER_COMPLETE){
            order.setStatus(OrderStatus.REQ_CANCEL);
            orderRepository.save(order);
        }
        else{
            throw new RuntimeException("배송중인 상품으로 주문 취소가 불가능합니다."); // 에러보다는 클라이언트한테 메시지 던지게 리팩터링 예정
        }
    }

    public void refundOrder(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow();
        if(order.getStatus()==OrderStatus.FINISH_SHIPPING){
            order.setStatus(OrderStatus.REQ_REFUND);
            orderRepository.save(order);
        }
        else{
            throw new RuntimeException("반품이 불가능한 상태입니다.");
        }
    }

    @Transactional
    public void exceedOrderDay() {
        List<Order> allOrders = orderRepository.findAll();

        LocalDateTime currentTime = LocalDateTime.now();
        LocalDateTime before24Hours = currentTime.minusHours(24);

        // 모든 주문에 대한 상태 업데이트 및 처리
        for (Order order : allOrders) {
            OrderStatus currentStatus = order.getStatus();
            boolean isMoreThan24HoursAgo = order.getUpdatedAt().isBefore(before24Hours);

            if (currentStatus == OrderStatus.ORDER_COMPLETE && isMoreThan24HoursAgo) {
                order.setStatus(OrderStatus.SHIPPING);
            } else if (currentStatus == OrderStatus.SHIPPING && isMoreThan24HoursAgo) {
                order.setStatus(OrderStatus.FINISH_SHIPPING);
            } else if (currentStatus == OrderStatus.REQ_REFUND && isMoreThan24HoursAgo) {
                order.setStatus(OrderStatus.FINISH_REFUND);
                returnStock(order);
            } else if (currentStatus == OrderStatus.REQ_CANCEL && isMoreThan24HoursAgo) {
                order.setStatus(OrderStatus.ORDER_CANCEL);
                returnStock(order);
            }

            orderRepository.save(order);
        }
    }

    private void returnStock(Order order){
        List<Cart> cartList = cartRepository.findAllByOrderId(order.getOrderId());
        for(Cart cart:cartList){
            Product product = productRepository.findById(cart.getProductId()).orElseThrow();
            product.setStock(product.getStock()+cart.getCount());
            productRepository.save(product);
        }
    }
}
