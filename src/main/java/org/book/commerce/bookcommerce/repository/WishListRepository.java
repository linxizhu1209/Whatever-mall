package org.book.commerce.bookcommerce.repository;

import org.book.commerce.bookcommerce.repository.entity.WishList;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WishListRepository extends JpaRepository<WishList,Long> {
}
