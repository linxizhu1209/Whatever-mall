package org.book.commerce.bookcommerce.repository;

import org.book.commerce.bookcommerce.repository.entity.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeliveryRepository extends JpaRepository<Delivery,Long> {
}
