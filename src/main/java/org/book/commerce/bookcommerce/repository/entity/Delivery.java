package org.book.commerce.bookcommerce.repository.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name="delivery")
public class Delivery extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="delivery_id")
    private Long deliveryId;

    @Column(name="status")
    @Enumerated(EnumType.ORDINAL)
    private DeliveryStatus status;

    @Column(name="order_id")
    private Long orderId;
}
