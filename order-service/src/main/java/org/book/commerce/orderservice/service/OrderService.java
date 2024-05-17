package org.book.commerce.orderservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.book.commerce.orderservice.dto.ReqBuyProduct;
import org.book.commerce.common.exception.ConflictException;
import org.book.commerce.common.exception.NotAcceptException;
import org.book.commerce.common.exception.NotFoundException;
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

    public List<OrderlistDto> getOrderList(String userEmail) {
        List<Order> orderList = orderRepository.findAllByUserEmailAndStatusNot(userEmail,OrderStatus.WAITING_PAYING);
        ArrayList<OrderlistDto> orderlistDtos = new ArrayList<>();
        for (Order order : orderList) {
            List<ProductOrder> productOrderList = productOrderRepository.findAllByOrderId(order.getOrderId());
            long[] productIdArr = productOrderList.stream().map(ProductOrder::getProductId).mapToLong(i -> i).toArray();
            List<ProductFeignResponse> productFeignlist = orderProductFeignClient.findProductByProductId(productIdArr);
            List<OrderProductListDto> orderProductListDtos = new ArrayList<>();
            for (ProductFeignResponse product : productFeignlist) {
                int orderCount = productOrderRepository.findByProductIdAndOrderId(product.productId(), order.getOrderId()).getCount();
                orderProductListDtos.add(OrderProductListDto.builder().productName(product.name())
                        .price(product.price()).productId(product.productId()).count(orderCount).build());
            }
            orderlistDtos.add(OrderlistDto.builder().orderId(order.getOrderId())
                    .orderStatus(order.getStatus()).orderDate(order.getCreatedAt())
                    .orderProductList(orderProductListDtos).build());
        }
        orderlistDtos.sort(((o1, o2) -> o2.getOrderDate().compareTo(o1.getOrderDate())));
        return orderlistDtos;
    }

    public void cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new NotFoundException("요청한 주문을 찾을 수 없습니다. 문제 주문 번호: " + orderId));
        if (order.getStatus() == OrderStatus.ORDER_COMPLETE) {
            order.setStatus(OrderStatus.REQ_CANCEL);
            orderRepository.save(order);
        } else {
            throw new NotAcceptException("배송중인 상품으로 주문 취소가 불가능합니다.");
        }
    }

    public void refundOrder(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new NotFoundException("존재하지 않는 주문입니다."));
        if (order.getStatus() == OrderStatus.FINISH_SHIPPING) {
            order.setStatus(OrderStatus.REQ_REFUND);
            orderRepository.save(order);
        } else {
            throw new NotAcceptException("반품이 불가능한 상태입니다.");
        }
    }

    @Transactional
    public void exceedOrderDay() {
        List<Order> orderlist = orderRepository.findAllOlderThanLast24HoursWithSpecificStatus(OrderStatus.ORDER_COMPLETE, OrderStatus.REQ_CANCEL, OrderStatus.SHIPPING, OrderStatus.REQ_REFUND);

        // 모든 주문에 대한 상태 업데이트 및 처리
        for (Order order : orderlist) {
            OrderStatus currentStatus = order.getStatus();
            if (currentStatus == OrderStatus.ORDER_COMPLETE) {
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
    @Transactional
    public OrderResultDto orderCartList(String userEmail) {
        Order order = Order.builder().userEmail(userEmail)
                .status(OrderStatus.WAITING_PAYING)
                .build();
        Long orderId = orderRepository.save(order).getOrderId();
        ArrayList<CartOrderFeignResponse> cartList = cartOrderFeignClient.findCartListByUserEmail(userEmail);
        ArrayList<OrderProductCountFeignRequest> orderProductCountList = new ArrayList<>();
        for (CartOrderFeignResponse cart : cartList) {
            orderProductCountList.add(new OrderProductCountFeignRequest(cart.productId(), cart.count()));
        }
        orderProductFeignClient.minusStockList(orderProductCountList); // 재고 감소 로직

        ArrayList<ProductOrder> productOrders = new ArrayList<>();
        for (CartOrderFeignResponse cart : cartList) {
            ProductOrder productOrder = ProductOrder.builder().productId(cart.productId())
                    .orderId(orderId).count(cart.count()).build();
            productOrders.add(productOrder);
        }
        ;
        productOrderRepository.saveAll(productOrders);
        cartOrderFeignClient.deleteAllCart(userEmail);

        return new OrderResultDto(orderId);
    }

    @Transactional
    public OrderResultDto orderProduct(String userEmail, ReqBuyProduct reqBuyProduct) {
            orderProductFeignClient.minusStock(reqBuyProduct.getProductId(), new OrderProductCountFeignRequest(reqBuyProduct.getProductId(), reqBuyProduct.getQuantity()));
            log.info("재고가 성공적으로 감소하였습니다.");
            Order order = Order.builder().status(OrderStatus.WAITING_PAYING).userEmail(userEmail).build();
            Long orderId = orderRepository.save(order).getOrderId();
            ProductOrder productOrder = ProductOrder.builder().productId(reqBuyProduct.getProductId()).count(reqBuyProduct.getQuantity())
                    .orderId(orderId).build();
            productOrderRepository.save(productOrder);
            return new OrderResultDto(orderId);
    }

    @Transactional
    public boolean payOrder(Long orderId, PayInfo payInfo) {
        if (payInfo.getIsCanceled()) {
            Order order = findOrderById(orderId);
            order.setStatus(OrderStatus.ORDER_CANCEL);
            returnStock(order);
            return false;
        } else { // 결제 진행
            if (payInfo.getIsLimitExcess()) { //한도 초과인 경우(고객 귀책)
                Order order = findOrderById(orderId);
                order.setStatus(OrderStatus.ORDER_CANCEL);
                returnStock(order);
                orderRepository.save(order);
                throw new ConflictException("한도초과로 결제가 불가능합니다. 주문이 취소되었습니다.");
            } else { //한도초과도 아니고 정상적으로 결제된 경우
                Order order = findOrderById(orderId);
                order.setStatus(OrderStatus.ORDER_COMPLETE);
                return true;
            }
        }
    }

    private Order findOrderById(Long orderId) {
        return orderRepository.findById(orderId).orElseThrow(() -> new NotFoundException("존재하지 않는 주문입니다."));
    }

    private void returnStock(Order order) {
        List<ProductOrder> productOrderList = productOrderRepository.findAllByOrderId(order.getOrderId());
        ArrayList<OrderProductCountFeignRequest> orderProductCountList = new ArrayList<>();
        if(productOrderList.size()==1) {
            ProductOrder productOrder = productOrderList.getFirst();
            OrderProductCountFeignRequest orderProduct = new OrderProductCountFeignRequest(productOrder.getProductId(), productOrder.getCount());
            orderProductFeignClient.plusStock(orderProduct.productId(), orderProduct);
        }
        else { // 장바구니 => 주문 경로는 한 주문에 여러 물품 존재 가능/ 상세페이지 => 주문 경로는 한 주문에 물품 한개이므로 두 개 로직을 구분하기위함
            for (ProductOrder productOrder : productOrderList) {
                orderProductCountList.add(new OrderProductCountFeignRequest(productOrder.getProductId(), productOrder.getCount()));
            }
            orderProductFeignClient.plusStockList(orderProductCountList);
        }
        log.info("재고가 성공적으로 변경되었습니다.");
    }

    @Transactional
    public void overPaymentDeadLine() {
        List<Order> orderList = orderRepository.findAllOlderThan10MinWithWatingPayingStatus(OrderStatus.WAITING_PAYING);
        for (Order order : orderList) {
            order.setStatus(OrderStatus.ORDER_CANCEL);
            returnStock(order);
        }
    }

}
