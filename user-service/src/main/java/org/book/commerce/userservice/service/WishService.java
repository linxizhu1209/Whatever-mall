package org.book.commerce.userservice.service;

import lombok.RequiredArgsConstructor;
import org.book.commerce.common.dto.CommonResponseDto;
import org.book.commerce.common.exception.ConflictException;
import org.book.commerce.common.exception.NotFoundException;
import org.book.commerce.userservice.domain.WishList;
import org.book.commerce.userservice.dto.ProductFeignResponse;
import org.book.commerce.userservice.repository.WishListRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WishService {
    private final WishListRepository wishListRepository;
    private final ProductFeignClient productFeignClient;

    @Transactional
    public CommonResponseDto addWish(String userEmail, Long productId) {
        if(wishListRepository.existsByUserEmailAndProductId(userEmail, productId)){
           throw new ConflictException("이미 찜 등록되어있는 상품입니다.");
        }
        productFeignClient.isExistProduct(productId);
        WishList wishList = WishList.builder().productId(productId).userEmail(userEmail).build();
        wishListRepository.save(wishList);
        return CommonResponseDto.builder().statusCode(200).success(true).message("찜 목록에 추가되었습니다!").build();
    }

    @Transactional
    public CommonResponseDto deleteWish(String userEmail,Long productId) {
        WishList wishList = wishListRepository.findWishListByProductIdAndUserEmail(productId, userEmail).orElseThrow(()->new NotFoundException("위시리스트에 존재하지 않는 물품입니다."));
        wishListRepository.delete(wishList);
        return CommonResponseDto.builder().statusCode(200).success(true).message("찜 목록에서 삭제되었습니다!").build();
    }

    @Transactional
    public List<ProductFeignResponse> getWishList(String userEmail) {
        List<WishList> wishLists = wishListRepository.findAllByUserEmail(userEmail);
        long[] productIdList = wishLists.stream().map(WishList::getProductId).mapToLong(i->i).toArray();
        return productFeignClient.findProductByProductId(productIdList);
        }
}
