package org.book.commerce.bookcommerce.domain.wishList.domain;

import jakarta.persistence.*;
import lombok.*;
import org.book.commerce.bookcommerce.common.entity.BaseEntity;

@Entity
@Setter
@Builder
@AllArgsConstructor
@Getter
@NoArgsConstructor
@Table(name="wishlist")
public class WishList extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="wishlist_id")
    private Long wishlistId;

    @Column(name="user_email")
    private String userEmail;

    @Column(name="product_id")
    private Long productId;



}
