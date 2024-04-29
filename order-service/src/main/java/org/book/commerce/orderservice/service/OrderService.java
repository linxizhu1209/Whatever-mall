package org.book.commerce.orderservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.book.commerce.cartservice.service.CartService;
import org.book.commerce.common.entity.ErrorCode;
import org.book.commerce.common.exception.CommonException;
import org.book.commerce.common.exception.ConflictException;
import org.book.commerce.common.exception.NotAcceptException;
import org.book.commerce.common.exception.NotFoundException;
import org.book.commerce.cartservice.domain.Cart;
import org.book.commerce.common.security.CustomUserDetails;
import org.book.commerce.orderservice.domain.Order;
import org.book.commerce.orderservice.domain.OrderStatus;
import org.book.commerce.orderservice.domain.ProductOrder;
import org.book.commerce.orderservice.dto.*;
import org.book.commerce.orderservice.repository.OrderRepository;
import org.book.commerce.orderservice.repository.ProductOrderRepository;
import org.book.commerce.productservice.domain.Product;
import org.book.commerce.productservice.service.ProductService;
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

    @Transactional
    public OrderResultDto payOrder(CustomUserDetails customUserDetails) {
        String userId = customUserDetails.getUsername();
        // todo 회원 email을 넘겨주면 장바구니 목록 불러오도록 (장바구니에는 productId, count 갖고오면됨)
        List<CartOrderFeignResponse> cartList = cartOrderFeignClient.findCartListByUserEmail(userId);
        Order order = Order.builder().userEmail(userId).status(OrderStatus.ORDER_COMPLETE).build();
        Long orderId = orderRepository.save(order).getOrderId();
        ArrayList<ProductOrder> productOrders = new ArrayList<>();
        ArrayList<OrderProductCountFeignRequest> orderProductCountList = new ArrayList<>();
        for(CartOrderFeignResponse cart:cartList) {
            ProductOrder productOrder = ProductOrder.builder().productId(cart.productId())
                    .orderId(orderId).count(cart.count()).build();
            productOrders.add(productOrder);
            orderProductCountList.add(new OrderProductCountFeignRequest(cart.productId(), cart.count()));
        };
        productOrderRepository.saveAll(productOrders);
        // 위에까지는 for문으로 돌면됨 cartList를 돌면서 생성 방식. 그치만 save를 한번에 하도록 list에 추가하는 식으로 하기
        // todo count를 넘겨주면 재고 감소시키도록 (minusStock) => 재고 감소시키고 물품 저장까지
        orderProductFeignClient.minusStock(orderProductCountList);
        // 위에까지는 productId랑 count를 넘겨서 재고 감소시키도록 호출. 그런데 한번에 넘길 수 있도록 ..
        cartOrderFeignClient.deleteAllCart(userId);
        // 위에는 userEmail 넘기면 그 유저의 장바구니는 다 지워버리도록(장바구니에서 주문이 가능하고, 한번주문할때 장바구니에 들어있는 모든 물품을 주문하는것이므로)
        return new OrderResultDto(orderId);
    }


    public List<OrderlistDto> getOrderList(CustomUserDetails customUserDetails) {
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
}
