package com.uade.tpo.pixelpoint.repository.cart;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uade.tpo.pixelpoint.entity.cart.CartItem;

public interface CartItemsRepository extends JpaRepository<CartItem, Long> {
    
    /**
     * Busca un item específico en un carrito por IDs del carrito y listing
     */
    Optional<CartItem> findByCartIdAndListingId(Long cartId, Long listingId);
    
    /**
     * Elimina todos los items de un carrito específico
     */
    void deleteByCartId(Long cartId);
    
}
