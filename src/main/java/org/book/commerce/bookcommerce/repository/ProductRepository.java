package org.book.commerce.bookcommerce.repository;

import org.book.commerce.bookcommerce.repository.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
