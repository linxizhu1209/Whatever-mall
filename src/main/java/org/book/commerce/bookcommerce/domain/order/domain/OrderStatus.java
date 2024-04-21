package org.book.commerce.bookcommerce.domain.order.domain;

public enum OrderStatus {
    ORDER_COMPLETE("주문완료"),
    REQ_CANCEL("취소신청"),
    ORDER_CANCEL("주문취소"),
    SHIPPING("배송중"),
    FINISH_SHIPPING("배송완료"),
    REQ_REFUND("반품신청"),
    FINISH_REFUND("반품완료");


    private final String status;

    OrderStatus(String status) {
        this.status = status;
    }

    public static OrderStatus valueOfTerm(String statusName) throws Exception {
        for(OrderStatus status:values()){
            if(statusName.equals(status.status)){
                return status;
            }
        }
        throw new Exception("존재하지 않는 상태입니다.");
    }
}
