package org.book.commerce.bookcommerce.domain.order.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.book.commerce.bookcommerce.common.entity.BaseEntity;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name="product_order")
public class ProductOrder extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="product_order_id")
    private Long productOrderId;

    @Column(name="count")
    private int count;

    @Column(name="order_id")
    private Long orderId;

    @Column(name="product_id")
    private Long productId;


}
