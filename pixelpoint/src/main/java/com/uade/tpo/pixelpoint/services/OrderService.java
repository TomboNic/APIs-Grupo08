package com.uade.tpo.pixelpoint.services;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.uade.tpo.pixelpoint.entity.cart.Order;
import com.uade.tpo.pixelpoint.entity.cart.OrderStatus;


public interface OrderService {

    Order createFromCart(Long userId);
    Optional<Order> getById(Long orderId);
    Page<Order> listByBuyer(Long buyerId, Pageable pageable);
    Order updateStatus(Long orderId, OrderStatus newStatus);
}
