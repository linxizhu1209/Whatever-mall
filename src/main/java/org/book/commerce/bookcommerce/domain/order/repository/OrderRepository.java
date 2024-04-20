package org.book.commerce.bookcommerce.domain.order.repository;

import org.book.commerce.bookcommerce.domain.order.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order,Long> {
}
