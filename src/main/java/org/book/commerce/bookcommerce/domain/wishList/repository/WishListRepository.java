package org.book.commerce.bookcommerce.domain.wishList.repository;

import org.book.commerce.bookcommerce.domain.wishList.domain.WishList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WishListRepository extends JpaRepository<WishList,Long> {

    Optional<WishList> findWishListByProductId(Long productId);
    boolean existsByUserEmailAndAndProductId(String email,Long productId);
    List<WishList> findAllByUserEmail(String username);
}
