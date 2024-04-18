package org.book.commerce.bookcommerce.repository.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Entity
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name="wishlist")
public class WishList extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="wishlist_id")
    private Long wishlistId;

    @Column(name="user_id")
    private Long userId;

    @Column(name="product_id")
    private Long productId;



}
