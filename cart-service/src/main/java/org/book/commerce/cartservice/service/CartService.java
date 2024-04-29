package org.book.commerce.cartservice.service;

import lombok.RequiredArgsConstructor;
import org.book.commerce.cartservice.dto.AddCartResult;
import org.book.commerce.cartservice.dto.CartListDto;
import org.book.commerce.cartservice.dto.CartOrderFeignResponse;
import org.book.commerce.cartservice.dto.CartProductFeignResponse;
import org.book.commerce.cartservice.repository.CartRepository;
import org.book.commerce.common.exception.ConflictException;
import org.book.commerce.cartservice.domain.Cart;
import org.book.commerce.common.exception.NotFoundException;
import org.book.commerce.common.security.CustomUserDetails;
import org.book.commerce.productservice.domain.Product;
import org.book.commerce.productservice.service.ProductService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class CartService {

    private final CartRepository cartRepository;
    private final CartProductFeignClient cartProductFeignClient;

    public AddCartResult addCart(CustomUserDetails customUserDetails, Long productId, int count) {
        if(cartRepository.existsByUserEmailAndProductId(customUserDetails.getUsername(),productId)){
            throw new ConflictException("이미 장바구니에 있는 제품입니다.");
        }
        Cart cart = Cart.builder().productId(productId)
                .userEmail(customUserDetails.getUsername())
                .count(count).build();
        Long cartId = cartRepository.save(cart).getCartId();
        return new AddCartResult(cartId);
    }
    
    public void deleteCart(Long cartId){
        Cart cart = cartRepository.findById(cartId).orElseThrow(()->new NotFoundException("요청한 장바구니를 찾을 수 없습니다"));
        cartRepository.delete(cart);
    }

    public void updateCart(Long cartId, int count) {
        Cart cart = cartRepository.findById(cartId).orElseThrow(()->new NotFoundException("요청한 장바구니를 찾을 수 없습니다"));
        cart.setCount(count);
        cartRepository.save(cart);
    }

    public List<CartListDto> getCartList(CustomUserDetails customUserDetails) {
        String email = customUserDetails.getUsername();
        List<Cart> cartList = cartRepository.findAllByUserEmail(email);
        long[] productIdList = cartList.stream().map(Cart::getProductId).mapToLong(i->i).toArray();
        List<CartProductFeignResponse> cartProductFeignResponses = cartProductFeignClient.findCartProductByProductId(productIdList);
        List<CartListDto> cartListDtos = new ArrayList<>();
        for(CartProductFeignResponse product:cartProductFeignResponses){
            Cart cart = cartRepository.findByUserEmailAndProductId(email, product.productId());
            cartListDtos.add(CartListDto.builder().productName(product.name())
                    .productId(product.productId()).price(product.price())
                    .count(cart.getCount()).build());
        }
        return cartListDtos;
    }

    public List<CartOrderFeignResponse> findCartList(String userId) {
        List<Cart> cartList = cartRepository.findAllByUserEmail(userId);
        ArrayList<CartOrderFeignResponse> cartOrderlist = new ArrayList<>();
        for(Cart cart:cartList){
            cartOrderlist.add(new CartOrderFeignResponse(cart.getProductId(),cart.getCount(),cart.getCartId()));
        }
        return cartOrderlist;
    }

    @Transactional
    public void deleteAllCart(String userEmail) {
        cartRepository.deleteAllByUserEmail(userEmail);
    }
}
