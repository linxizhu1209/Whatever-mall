package org.book.commerce.orderservice.repository;

import org.book.commerce.orderservice.domain.ProductOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductOrderRepository extends JpaRepository<ProductOrder,Long> {
    List<ProductOrder> findAllByOrderId(Long orderId);
    ProductOrder findByProductIdAndOrderId(Long productId, Long orderId);
}
