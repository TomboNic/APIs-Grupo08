package com.uade.tpo.pixelpoint.controllers;

import java.net.URI;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
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
import com.uade.tpo.pixelpoint.repository.marketplace.UserRepository;
import com.uade.tpo.pixelpoint.services.OrderService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    @Autowired
    OrderService orderService;
    @Autowired
    OrderRepository orderRepository;

    @Autowired
    UserRepository userRepository;

    @PostMapping("/me/checkout")
    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<OrderResponse> checkoutMe(Authentication auth) {
        Long userId = getCurrentUserId(auth);
        Order order = orderService.createFromCart(userId);
        return ResponseEntity
                .created(URI.create("/orders/" + order.getId()))
                .body(toResponse(order));
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<?> myOrders(
            Authentication auth,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Long userId = getCurrentUserId(auth);
        var ordersPage = orderService.listByBuyer(userId, PageRequest.of(page, size));

        var dtoPage = ordersPage.map(this::toResponse);
        return ResponseEntity.ok(dtoPage);
    }

    // ✅ Obtener una orden por ID (buyer dueño o admin)
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('BUYER','ADMIN')")
    public ResponseEntity<?> get(@PathVariable Long id, Authentication auth) {
        return orderRepository.findById(id)
                .map(order -> {
                    if (!isOwnerOrAdmin(order, auth)) {
                        return ResponseEntity.status(403).body("No autorizado para ver esta orden");
                    }
                    return ResponseEntity.ok(toResponse(order));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // =========================
    // Helpers
    // =========================
    private Long getCurrentUserId(Authentication auth) {
        return userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + auth.getName()))
                .getId();
    }

    private boolean isOwnerOrAdmin(Order order, Authentication auth) {
        boolean isAdmin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        Long userId = null;
        try {
            userId = getCurrentUserId(auth);
        } catch (Exception ignored) {
        }
        return isAdmin || (order.getBuyer() != null && order.getBuyer().getId().equals(userId));
    }

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
                .items(order.getItems().stream().map(this::toItemResponse).collect(Collectors.toList()))
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