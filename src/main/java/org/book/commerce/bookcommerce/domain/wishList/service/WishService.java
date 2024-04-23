package org.book.commerce.bookcommerce.domain.wishList.service;

import lombok.RequiredArgsConstructor;
import org.book.commerce.bookcommerce.common.exception.ConflictException;
import org.book.commerce.bookcommerce.common.exception.NotFoundException;
import org.book.commerce.bookcommerce.domain.product.domain.Product;
import org.book.commerce.bookcommerce.domain.product.repository.ProductRepository;
import org.book.commerce.bookcommerce.domain.user.domain.CustomUserDetails;
import org.book.commerce.bookcommerce.domain.wishList.domain.WishList;
import org.book.commerce.bookcommerce.domain.wishList.dto.WishListDto;
import org.book.commerce.bookcommerce.domain.wishList.repository.WishListRepository;
import org.book.commerce.bookcommerce.domain.wishList.service.mapper.WishListMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WishService {
    private final WishListRepository wishListRepository;
    private final ProductRepository productRepository;
    public ResponseEntity addWish(CustomUserDetails customUserDetails, Long productId) {
        if(wishListRepository.existsByUserEmailAndAndProductId(customUserDetails.getUsername(), productId)){
           throw new ConflictException("이미 찜 등록되어있는 상품입니다.");
        }
        WishList wishList = WishList.builder().productId(productId).userEmail(customUserDetails.getUsername()).build();
        wishListRepository.save(wishList);
        return ResponseEntity.status(HttpStatus.OK).body("찜 목록에 추가되었습니다!");
        // todo 이미 추가되어잇는 상품이면 추가되지 않도록 해야함
    }

    public ResponseEntity deleteWish(Long productId) {
        WishList wishList = wishListRepository.findWishListByProductId(productId).orElseThrow(()->new NotFoundException("위시리스트에 존재하지 않는 물품입니다."));
        wishListRepository.delete(wishList);
        return ResponseEntity.status(HttpStatus.OK).body("찜 목록에서 삭제되었습니다!");
    }

    // todo Product 도메인과 통신해야할 부분. 어떻게 해야할 지 고민
    public ResponseEntity<List<WishListDto>> getWishList(CustomUserDetails customUserDetails) {
        List<WishList> wishLists = wishListRepository.findAllByUserEmail(customUserDetails.getUsername());
        List<Product> productList = wishLists.stream().map(wishList->productRepository.findById(wishList.getProductId()).orElseThrow(()->new NotFoundException("일치하는 제품을 찾을 수 없습니다."))).toList(); // 이후 msa 시 어떻게 통신할지 고민해봐야할 부분
        List<WishListDto> wishListDtos = productList.stream().map(WishListMapper.INSTANCE::ProductEntityToWishListDto).toList();
        return ResponseEntity.status(HttpStatus.OK).body(wishListDtos);
    }
}
