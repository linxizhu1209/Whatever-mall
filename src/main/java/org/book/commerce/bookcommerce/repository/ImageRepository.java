package org.book.commerce.bookcommerce.repository;

import org.book.commerce.bookcommerce.repository.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImageRepository extends JpaRepository<Image,Long> {
    List<Image> findAllByProductId(Long productId);
}
