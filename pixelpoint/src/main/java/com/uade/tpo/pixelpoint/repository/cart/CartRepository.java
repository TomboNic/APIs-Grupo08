package com.uade.tpo.pixelpoint.repository.cart;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uade.tpo.pixelpoint.entity.cart.Cart;

public interface CartRepository extends JpaRepository<Cart, Long>{
    
}
