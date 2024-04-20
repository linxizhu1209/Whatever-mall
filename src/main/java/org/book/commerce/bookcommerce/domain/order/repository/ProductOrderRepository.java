package org.book.commerce.bookcommerce.domain.order.repository;

import org.book.commerce.bookcommerce.domain.order.domain.ProductOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductOrderRepository extends JpaRepository<ProductOrder,Long> {
}
