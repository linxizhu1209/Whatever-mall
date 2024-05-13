package org.book.commerce.userservice.service;

import lombok.RequiredArgsConstructor;
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
    public ResponseEntity addWish(String userEmail, Long productId) {
        if(wishListRepository.existsByUserEmailAndProductId(userEmail, productId)){
           throw new ConflictException("이미 찜 등록되어있는 상품입니다.");
        }
        WishList wishList = WishList.builder().productId(productId).userEmail(userEmail).build();
        wishListRepository.save(wishList);
        return ResponseEntity.status(HttpStatus.OK).body("찜 목록에 추가되었습니다!");
        // todo 이미 추가되어잇는 상품이면 추가되지 않도록 해야함
    }

    @Transactional
    public ResponseEntity deleteWish(String userEmail,Long productId) {
        WishList wishList = wishListRepository.findWishListByProductIdAndUserEmail(productId, userEmail).orElseThrow(()->new NotFoundException("위시리스트에 존재하지 않는 물품입니다."));
        wishListRepository.delete(wishList);
        return ResponseEntity.status(HttpStatus.OK).body("찜 목록에서 삭제되었습니다!");
    }

    // todo Product 도메인과 통신해야할 부분. 어떻게 해야할 지 고민

    @Transactional
    public ResponseEntity<List<ProductFeignResponse>> getWishList(String userEmail) {
        List<WishList> wishLists = wishListRepository.findAllByUserEmail(userEmail);
        // productID를 넘겨주면 product에서 필요한 것을 담은 dto객체를 넘겨주게끔(productId,name,가격,이미지경로,이미지이름 필요)
        long[] productIdList = wishLists.stream().map(WishList::getProductId).mapToLong(i->i).toArray();
        List<ProductFeignResponse> productFeignResponseList = productFeignClient.findProductByProductId(productIdList);
        return ResponseEntity.status(HttpStatus.OK).body(productFeignResponseList);
    }
}
