package org.book.commerce.bookcommerce.domain.cart.domain;

public enum CartStatus {

    ORDER_INCOMPLETE("주문전"),
    ORDER_COMPLETE("주문완료");

    private final String status;

    CartStatus(String status){ this.status=status; }

    public static CartStatus valueOfTerm(String statusName){
        for(CartStatus status:values()){
            if(statusName.equals(status.status)){
                return status;
            }
        }
        throw new RuntimeException("존재하지 않는 상태입니다");
    }


}
