package org.book.commerce.orderservice.repository;

import org.book.commerce.orderservice.domain.Order;
import org.book.commerce.orderservice.domain.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order,Long> {

    List<Order> findAllByUserEmailAndStatusNot(String userEmail,OrderStatus orderStatus);

    @Query(value = "SELECT * FROM `orders` o WHERE (o.status IN (?1,?2,?3,?4)) AND (updated_at <= DATE_SUB(NOW(), INTERVAL 24 HOUR))",nativeQuery = true)
    List<Order> findAllOlderThanLast24HoursWithSpecificStatus(OrderStatus status1, OrderStatus status2, OrderStatus status3, OrderStatus status4);

    @Query(value = "SELECT * FROM orders o WHERE (o.status IN (?1)) AND (updated_at <= DATE_SUB(NOW(), INTERVAL 10 MINUTE))", nativeQuery = true)
    List<Order> findAllOlderThan10MinWithWatingPayingStatus(OrderStatus status);
}
