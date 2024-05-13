package org.book.commerce.cartservice.service;

import lombok.RequiredArgsConstructor;
import org.book.commerce.cartservice.dto.AddCartResult;
import org.book.commerce.cartservice.dto.CartListDto;
import org.book.commerce.cartservice.dto.CartOrderFeignResponse;
import org.book.commerce.cartservice.dto.CartProductFeignResponse;
import org.book.commerce.cartservice.repository.CartRepository;
import org.book.commerce.cartservice.domain.Cart;
import org.book.commerce.common.exception.ConflictException;
import org.book.commerce.common.exception.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class CartService {

    private final CartRepository cartRepository;
    private final CartProductFeignClient cartProductFeignClient;

    @Transactional
    public AddCartResult addCart(String userEmail, Long productId, int count) {
        if(cartRepository.existsByUserEmailAndProductId(userEmail,productId)){
            throw new ConflictException("이미 장바구니에 있는 제품입니다.");
        }
        Cart cart = Cart.builder().productId(productId)
                .userEmail(userEmail)
                .count(count).build();
        Long cartId = cartRepository.save(cart).getCartId();
        return new AddCartResult(cartId);
    }
    @Transactional
    public void deleteCart(Long cartId){
        Cart cart = cartRepository.findById(cartId).orElseThrow(()->new NotFoundException("요청한 장바구니를 찾을 수 없습니다"));
        cartRepository.delete(cart);
    }
    @Transactional
    public void updateCart(Long cartId, int count) {
        Cart cart = cartRepository.findById(cartId).orElseThrow(()->new NotFoundException("요청한 장바구니를 찾을 수 없습니다"));
        cart.setCount(count);
        cartRepository.save(cart);
    }
    @Transactional
    public List<CartListDto> getCartList(String userEmail) {
        List<Cart> cartList = cartRepository.findAllByUserEmail(userEmail);
        long[] productIdList = cartList.stream().map(Cart::getProductId).mapToLong(i->i).toArray();
        List<CartProductFeignResponse> cartProductFeignResponses = cartProductFeignClient.findCartProductByProductId(productIdList);
        List<CartListDto> cartListDtos = new ArrayList<>();
        for(CartProductFeignResponse product:cartProductFeignResponses){
            Cart cart = cartRepository.findByUserEmailAndProductId(userEmail, product.productId());
            cartListDtos.add(CartListDto.builder().productName(product.name())
                    .productId(product.productId()).price(product.price())
                    .count(cart.getCount()).build());
        }
        return cartListDtos;
    }

    public ArrayList<CartOrderFeignResponse> findCartList(String userId) {
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
