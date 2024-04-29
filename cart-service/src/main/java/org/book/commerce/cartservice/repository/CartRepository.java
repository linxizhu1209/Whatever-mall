package org.book.commerce.cartservice.repository;

import org.book.commerce.cartservice.domain.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartRepository extends JpaRepository<Cart,Long> {

    List<Cart> findAllByUserEmail(String email);
    boolean existsByUserEmailAndProductId(String username, Long productId);

    Cart findByUserEmailAndProductId(String email, Long aLong);

    void deleteAllByUserEmail(String userEmail);
}
