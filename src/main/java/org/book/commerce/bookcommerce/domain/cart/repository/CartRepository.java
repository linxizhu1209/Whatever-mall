package org.book.commerce.bookcommerce.domain.cart.repository;

import org.book.commerce.bookcommerce.domain.cart.domain.Cart;
import org.book.commerce.bookcommerce.domain.cart.domain.CartStatus;
import org.book.commerce.bookcommerce.domain.order.domain.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartRepository extends JpaRepository<Cart,Long> {

    List<Cart> findAllByUserEmail(String email);
    List<Cart> findAllByUserEmailAndStatus(String email, CartStatus cartStatus);
    Cart findByProductId(Long productId);

    List<Cart> findAllByOrderId(Long orderId);

    boolean existsByUserEmailAndProductIdAndStatus(String useremail,Long productId, CartStatus status);
}
