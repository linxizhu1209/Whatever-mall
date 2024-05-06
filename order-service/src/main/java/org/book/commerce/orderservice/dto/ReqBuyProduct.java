package org.book.commerce.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReqBuyProduct implements Serializable {
    private Long productId;
    private int quantity;
}
