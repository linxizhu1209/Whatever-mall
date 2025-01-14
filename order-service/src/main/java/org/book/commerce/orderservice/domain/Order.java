package org.book.commerce.orderservice.domain;


import jakarta.persistence.*;
import lombok.*;
import org.book.commerce.common.entity.BaseEntity;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Table(name="orders")
public class Order extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="order_id")
    private Long orderId;

    @Column(name="status")
    @Enumerated(EnumType.ORDINAL)
    private OrderStatus status;

    @Column(name="user_email")
    private String userEmail;

}

