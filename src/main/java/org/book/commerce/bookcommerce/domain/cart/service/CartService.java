package org.book.commerce.bookcommerce.domain.cart.service;

import lombok.RequiredArgsConstructor;
import org.book.commerce.bookcommerce.domain.cart.domain.Cart;
import org.book.commerce.bookcommerce.domain.cart.dto.AddCartResult;
import org.book.commerce.bookcommerce.domain.cart.dto.CartListDto;
import org.book.commerce.bookcommerce.domain.cart.repository.CartRepository;
import org.book.commerce.bookcommerce.domain.cart.service.mapper.CartMapper;
import org.book.commerce.bookcommerce.domain.product.domain.Product;
import org.book.commerce.bookcommerce.domain.product.repository.ProductRepository;
import org.book.commerce.bookcommerce.domain.user.domain.CustomUserDetails;
import org.springframework.stereotype.Service;


import java.util.List;

@RequiredArgsConstructor
@Service
public class CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    public AddCartResult addCart(CustomUserDetails customUserDetails, Long productId, int count) {
        Cart cart = Cart.builder().productId(productId)
                .userEmail(customUserDetails.getUsername()).count(count).build();
        Long cartId = cartRepository.save(cart).getCartId();
        return new AddCartResult(cartId);
    }
    
    public void deleteCart(Long cartId){
        Cart cart = cartRepository.findById(cartId).orElseThrow();
        cartRepository.delete(cart);
    }

    public void updateCart(Long cartId, int count) {
        Cart cart = cartRepository.findById(cartId).orElseThrow();
        cart.setCount(count);
        cartRepository.save(cart);
    }

    public List<CartListDto> getCartList(CustomUserDetails customUserDetails) {
        String email = customUserDetails.getUsername();
        List<Cart> cartList = cartRepository.findAllByUserEmail(email);
        List<Product> productList = cartList.stream().map(list->productRepository.findById(list.getProductId()).orElseThrow()).toList();
        List<CartListDto> cartListDtos = productList.stream().map(CartMapper.INSTANCE::CartEntityToDto).toList();
        for(CartListDto cart:cartListDtos){
            Long thisProductId = cart.getProductId();
            Cart thiscart = cartRepository.findByProductId(thisProductId);
               cart.setCount(thiscart.getCount());
        }
        return cartListDtos;
    }
}
