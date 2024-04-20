package org.book.commerce.bookcommerce.domain.wishList.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.book.commerce.bookcommerce.common.entity.BaseEntity;

@Entity
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name="wishlist")
public class WishList extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="wishlist_id")
    private Long wishlistId;

    @Column(name="user_id")
    private Long userId;

    @Column(name="product_id")
    private Long productId;



}
