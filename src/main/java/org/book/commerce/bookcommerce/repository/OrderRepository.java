package org.book.commerce.bookcommerce.repository;

import org.book.commerce.bookcommerce.repository.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order,Long> {
}
