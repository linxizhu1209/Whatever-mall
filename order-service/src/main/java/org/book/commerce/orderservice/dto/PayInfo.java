package org.book.commerce.orderservice.dto;

import lombok.Getter;

@Getter
public class PayInfo {
    private Boolean isCanceled; //결제 취소할 것인지 여부
    private Boolean isLimitExcess; // 한도초과인지 여부 (원래는 이러면 안되지만,이탈 테스트를 위함)
}
