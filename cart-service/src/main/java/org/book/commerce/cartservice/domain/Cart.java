package org.book.commerce.cartservice.domain;

import jakarta.persistence.*;
import lombok.*;
import org.book.commerce.common.entity.BaseEntity;

@Entity
@AllArgsConstructor
@Builder
@NoArgsConstructor
@Setter
@Getter
public class Cart extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="cart_id")
    private Long cartId;

    @Column(name="product_id")
    private Long productId;

    @Column(name="user_email")
    private String userEmail;

    @Column(name="count")
    private int count;

}
