package com.uade.tpo.pixelpoint.repository.cart;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uade.tpo.pixelpoint.entity.cart.Cart;

public interface CartRepository extends JpaRepository<Cart, Long>{
    
    /**
     * Busca un carrito por el ID del usuario
     */
    Optional<Cart> findByUserId(Long userId);
}

