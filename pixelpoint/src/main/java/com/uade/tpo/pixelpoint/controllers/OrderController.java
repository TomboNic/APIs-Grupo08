package com.uade.tpo.pixelpoint.controllers;

import java.net.URI;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.uade.tpo.pixelpoint.entity.cart.Order;
import com.uade.tpo.pixelpoint.entity.cart.OrderItem;
import com.uade.tpo.pixelpoint.entity.dto.OrderItemResponse;
import com.uade.tpo.pixelpoint.entity.dto.OrderResponse;
import com.uade.tpo.pixelpoint.repository.cart.OrderRepository;
import com.uade.tpo.pixelpoint.services.OrderService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final OrderRepository orderRepository;

    // ✅ Crear una orden a partir del carrito
    @PostMapping("/from-cart")
    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<OrderResponse> createFromCart(@RequestParam Long userId) {
        Order order = orderService.createFromCart(userId);
        return ResponseEntity
                .created(URI.create("/orders/" + order.getId()))
                .body(toResponse(order));
    }

    // ✅ Obtener una orden por ID
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('BUYER','ADMIN')")
    public ResponseEntity<OrderResponse> get(@PathVariable Long id) {
        return orderRepository.findById(id)
                .map(order -> ResponseEntity.ok(toResponse(order)))
                .orElse(ResponseEntity.notFound().build());
    }

    // ✅ Helper: convertir entidad a DTO limpio
    private OrderResponse toResponse(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .buyerId(order.getBuyer() != null ? order.getBuyer().getId() : null)
                .status(order.getStatus().name())
                .subtotal(order.getSubtotal())
                .discountTotal(order.getDiscountTotal())
                .taxTotal(order.getTaxTotal())
                .grandTotal(order.getGrandTotal())
                .createdAt(order.getCreatedAt())
                .items(
                    order.getItems().stream()
                        .map(this::toItemResponse)
                        .collect(Collectors.toList())
                )
                .build();
    }

    private OrderItemResponse toItemResponse(OrderItem item) {
        return OrderItemResponse.builder()
                .id(item.getId())
                .listingId(item.getListingId())
                .sellerId(item.getSellerId())
                .title(item.getTitle())
                .unitPrice(item.getUnitPrice())
                .quantity(item.getQuantity())
                .lineTotal(item.getLineTotal())
                .build();
    }
}