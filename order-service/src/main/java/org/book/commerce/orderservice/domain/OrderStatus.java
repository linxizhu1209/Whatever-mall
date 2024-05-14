package org.book.commerce.orderservice.domain;

public enum OrderStatus {
    ORDER_COMPLETE("주문완료"),
    REQ_CANCEL("취소신청"),
    ORDER_CANCEL("주문취소"),
    SHIPPING("배송중"),
    FINISH_SHIPPING("배송완료"),
    REQ_REFUND("반품신청"),
    FINISH_REFUND("반품완료"),
    WAITING_PAYING("결제대기");

    private final String status;

    OrderStatus(String status) {
        this.status = status;
    }

}
