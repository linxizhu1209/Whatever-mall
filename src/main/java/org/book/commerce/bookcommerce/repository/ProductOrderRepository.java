package org.book.commerce.bookcommerce.repository;

import org.book.commerce.bookcommerce.repository.entity.ProductOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductOrderRepository extends JpaRepository<ProductOrder,Long> {
}
