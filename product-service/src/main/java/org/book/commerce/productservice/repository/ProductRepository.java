package org.book.commerce.productservice.repository;

import jakarta.persistence.LockModeType;
import org.book.commerce.productservice.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Product p where p.productId=:id")
    Optional<Product> findByIdWithPessimisticLock(Long id);
}
