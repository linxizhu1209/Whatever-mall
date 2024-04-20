package org.book.commerce.bookcommerce.domain.cart.repository;

import org.book.commerce.bookcommerce.domain.cart.domain.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartRepository extends JpaRepository<Cart,Long> {

    List<Cart> findAllByUserEmail(String email);
    Cart findByProductId(Long productId);
}
