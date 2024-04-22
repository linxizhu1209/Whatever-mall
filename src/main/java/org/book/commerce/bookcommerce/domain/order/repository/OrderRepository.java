package org.book.commerce.bookcommerce.domain.order.repository;

import org.book.commerce.bookcommerce.domain.order.domain.Order;
import org.book.commerce.bookcommerce.domain.order.domain.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order,Long> {
    List<Order> findAllByUserEmail(String userEmail);

    List<Order> findAllByStatus(OrderStatus status);
}
