package org.book.commerce.bookcommerce.domain.order.repository;

import org.book.commerce.bookcommerce.domain.order.domain.Order;
import org.book.commerce.bookcommerce.domain.order.domain.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order,Long> {
    List<Order> findAllByUserEmail(String userEmail);

    @Query(value = "SELECT * FROM `orders` o WHERE (o.status IN (?1,?2,?3,?4)) AND (updated_at <= DATE_SUB(NOW(), INTERVAL 24 HOUR))",nativeQuery = true)
    List<Order> findAllOlderThanLast24HoursWithSpecificStatus(OrderStatus status1, OrderStatus status2, OrderStatus status3, OrderStatus status4);

}
