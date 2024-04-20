package org.book.commerce.bookcommerce.domain.wishList.repository;

import org.book.commerce.bookcommerce.domain.wishList.domain.WishList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WishListRepository extends JpaRepository<WishList,Long> {
}
