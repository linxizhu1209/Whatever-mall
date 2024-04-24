package org.book.commerce.bookcommerce.domain.order.repository;

import org.book.commerce.bookcommerce.domain.order.domain.ProductOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductOrderRepository extends JpaRepository<ProductOrder,Long> {
    List<ProductOrder> findAllByOrderId(Long orderId);
}
