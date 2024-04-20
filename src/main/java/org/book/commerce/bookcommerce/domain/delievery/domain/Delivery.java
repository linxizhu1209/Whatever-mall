package org.book.commerce.bookcommerce.domain.delievery.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.book.commerce.bookcommerce.common.entity.BaseEntity;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name="delivery")
public class Delivery extends BaseEntity {
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
