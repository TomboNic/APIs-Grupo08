package com.uade.tpo.pixelpoint.services;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.uade.tpo.pixelpoint.entity.cart.Cart;
import com.uade.tpo.pixelpoint.entity.cart.Order;
import com.uade.tpo.pixelpoint.entity.dto.CartResponse;

public interface CartService {
    
    Cart getOrCreateCartByUserId(Long userId);
    Optional<Cart> getCartById(Long cartId);
    Page<Cart> getAllCarts(PageRequest pageRequest);
    Cart addItemToCart(Long userId, Long listingId, int quantity);
    Cart updateItemQuantity(Long userId, Long listingId, int newQuantity);
    Cart removeItemFromCart(Long userId, Long listingId);
    Cart clearCart(Long userId);
    void deleteCart(Long cartId);
    CartResponse convertToCartResponse(Cart cart);
    double calculateCartTotal(Cart cart);
    void validateUserCanHaveCart(Long userId);
    Order checkout(Long userId);
}
