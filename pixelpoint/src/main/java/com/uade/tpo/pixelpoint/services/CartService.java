package com.uade.tpo.pixelpoint.services;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.uade.tpo.pixelpoint.entity.cart.Cart;
import com.uade.tpo.pixelpoint.entity.dto.CartResponse;

public interface CartService {
    
    /**
     * Obtiene el carrito activo de un usuario. Si no existe, lo crea.
     */
    Cart getOrCreateCartByUserId(Long userId);
    
    /**
     * Obtiene un carrito por ID
     */
    Optional<Cart> getCartById(Long cartId);
    
    /**
     * Obtiene todos los carritos con paginación
     */
    Page<Cart> getAllCarts(PageRequest pageRequest);
    
    /**
     * Agrega un item al carrito. Si ya existe, actualiza la cantidad.
     */
    Cart addItemToCart(Long userId, Long listingId, int quantity);
    
    /**
     * Actualiza la cantidad de un item en el carrito
     */
    Cart updateItemQuantity(Long userId, Long listingId, int newQuantity);
    
    /**
     * Remueve un item específico del carrito
     */
    Cart removeItemFromCart(Long userId, Long listingId);
    
    /**
     * Vacía completamente el carrito (remueve todos los items)
     */
    Cart clearCart(Long userId);
    
    /**
     * Elimina un carrito completamente
     */
    void deleteCart(Long cartId);
    
    /**
     * Convierte un Cart a CartResponse con totales calculados
     */
    CartResponse convertToCartResponse(Cart cart);
    
    /**
     * Calcula el total del carrito
     */
    double calculateCartTotal(Cart cart);
    
    /**
     * Valida que el usuario sea BUYER (solo buyers pueden tener carrito)
     */
    void validateUserCanHaveCart(Long userId);
}
