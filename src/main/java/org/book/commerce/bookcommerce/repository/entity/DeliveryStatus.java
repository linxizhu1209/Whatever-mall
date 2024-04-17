package org.book.commerce.bookcommerce.repository.entity;

public enum DeliveryStatus {
    PREPARING("배송준비"),
    SHIPPING("배송중"),
    FINISH("배송완료");

    private final String status;

    DeliveryStatus(String status) {
        this.status = status;
    }

    public static DeliveryStatus valueOfTerm(String statusName) throws Exception {
        for(DeliveryStatus status:values()){
            if(statusName.equals(status.status)){
                return status;
            }
        }
        throw new Exception("존재하지 않는 상태입니다.");
    }

}
