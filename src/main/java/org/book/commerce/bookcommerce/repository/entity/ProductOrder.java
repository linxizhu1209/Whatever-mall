package org.book.commerce.bookcommerce.repository.entity;

import jakarta.persistence.*;

public class ProductOrder extends BaseEntity{
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
