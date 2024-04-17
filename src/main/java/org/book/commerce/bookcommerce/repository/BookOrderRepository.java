package org.book.commerce.bookcommerce.repository;

import org.book.commerce.bookcommerce.repository.entity.ProductOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookOrderRepository extends JpaRepository<ProductOrder,Long> {
}
