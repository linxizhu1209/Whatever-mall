package org.book.commerce.orderservice.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PaymentResultDto {
    private Long paymentId;

    public PaymentResultDto(Long paymentId) {
        this.paymentId = paymentId;
    }
}
