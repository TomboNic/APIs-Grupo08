package com.uade.tpo.pixelpoint.repository.cart;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uade.tpo.pixelpoint.entity.cart.CartItem;

public interface CartItemsRepository extends JpaRepository<CartItem, Long> {
}
