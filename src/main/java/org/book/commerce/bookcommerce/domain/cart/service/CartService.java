package org.book.commerce.bookcommerce.domain.cart.service;

import lombok.RequiredArgsConstructor;
import org.book.commerce.bookcommerce.common.exception.ConflictException;
import org.book.commerce.bookcommerce.common.exception.NotFoundException;
import org.book.commerce.bookcommerce.domain.cart.domain.Cart;
import org.book.commerce.bookcommerce.domain.cart.domain.CartStatus;
import org.book.commerce.bookcommerce.domain.cart.dto.AddCartResult;
import org.book.commerce.bookcommerce.domain.cart.dto.CartListDto;
import org.book.commerce.bookcommerce.domain.cart.repository.CartRepository;
import org.book.commerce.bookcommerce.domain.cart.service.mapper.CartMapper;
import org.book.commerce.bookcommerce.domain.product.domain.Product;
import org.book.commerce.bookcommerce.domain.product.repository.ProductRepository;
import org.book.commerce.bookcommerce.domain.user.domain.CustomUserDetails;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    public AddCartResult addCart(CustomUserDetails customUserDetails, Long productId, int count) {
        if(cartRepository.existsByUserEmailAndProductIdAndStatus(customUserDetails.getUsername(),productId,CartStatus.ORDER_INCOMPLETE)){
            throw new ConflictException("이미 장바구니에 있는 제품입니다.");
        }
        Cart cart = Cart.builder().productId(productId)
                .userEmail(customUserDetails.getUsername())
                .status(CartStatus.ORDER_INCOMPLETE).count(count).build();
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
        List<Cart> cartList = cartRepository.findAllByUserEmailAndStatus(email, CartStatus.ORDER_INCOMPLETE);
        List<CartListDto> cartListDtos = new ArrayList<>();
        for(Cart cart:cartList){
            Product product = productRepository.findById(cart.getProductId()).orElseThrow(()->new NotFoundException("존재하지 않는 물품입니다."));
            cartListDtos.add(CartListDto.builder().productName(product.getName())
                    .productId(product.getProductId()).price(product.getPrice())
                    .count(cart.getCount()).build());
        }
        return cartListDtos;
    }
}
