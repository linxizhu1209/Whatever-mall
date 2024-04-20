package org.book.commerce.bookcommerce.domain.delievery.repository;

import org.book.commerce.bookcommerce.domain.delievery.domain.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeliveryRepository extends JpaRepository<Delivery,Long> {
}
