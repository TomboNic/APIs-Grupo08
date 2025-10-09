package com.uade.tpo.pixelpoint.services;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.uade.tpo.pixelpoint.entity.cart.Cart;
import com.uade.tpo.pixelpoint.entity.cart.CartItem;
import com.uade.tpo.pixelpoint.entity.cart.Order;
import com.uade.tpo.pixelpoint.entity.cart.OrderItem;
import com.uade.tpo.pixelpoint.entity.cart.OrderStatus;
import com.uade.tpo.pixelpoint.entity.marketplace.Listing;
import com.uade.tpo.pixelpoint.entity.marketplace.User;
import com.uade.tpo.pixelpoint.repository.cart.CartItemsRepository;
import com.uade.tpo.pixelpoint.repository.cart.CartRepository;
import com.uade.tpo.pixelpoint.repository.cart.OrderRepository;
import com.uade.tpo.pixelpoint.repository.marketplace.ListingRepository;
import com.uade.tpo.pixelpoint.repository.marketplace.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final CartRepository cartRepository;
    private final CartItemsRepository cartItemsRepository;
    private final ListingRepository listingRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    public Order createFromCart(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalStateException("No se encontró el carrito del usuario " + userId));

        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new IllegalStateException("El carrito está vacío");
        }

        Order order = new Order();
        order.setBuyer(cart.getUser());
        order.setOrderNumber(generateOrderNumber());

        // ✅ FIX: enum externo
        order.setStatus(OrderStatus.PENDING_PAYMENT);

        double subtotal = 0.0;

        for (CartItem ci : cart.getItems()) {
            Listing listing = ci.getListing();

            OrderItem oi = new OrderItem();
            oi.setOrder(order);
            oi.setListingId(listing.getId());
            oi.setSellerId(listing.getSeller().getId());
            oi.setTitle(buildItemTitle(listing));
            double unit = listing.getEffectivePrice() != null
                    ? listing.getEffectivePrice().doubleValue()
                    : (listing.getPrice() != null ? listing.getPrice().doubleValue() : 0.0);
            oi.setUnitPrice(unit);
            oi.setQuantity(ci.getQuantity());
            oi.setLineTotal(unit * ci.getQuantity());

            order.getItems().add(oi);
            subtotal += oi.getLineTotal();
        }

        order.setSubtotal(subtotal);
        order.setDiscountTotal(0.0);
        order.setTaxTotal(0.0);
        order.setGrandTotal(subtotal);

        Order saved = orderRepository.save(order);

        // Opcional: borrar/vaciar carrito tras checkout
        cartRepository.findByUserId(userId).ifPresent(c -> {
            cartItemsRepository.deleteByCartId(c.getId());
            cartRepository.delete(c);
        });

        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Order> getById(Long orderId) {
        return orderRepository.findById(orderId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Order> listByBuyer(Long buyerId, Pageable pageable) {
        return orderRepository.findByBuyerIdOrderByCreatedAtDesc(buyerId, pageable);
    }

    @Override
    public Order updateStatus(Long orderId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Orden no encontrada: " + orderId));
        order.setStatus(newStatus);
        return orderRepository.save(order);
    }

    // =========================
    // HELPERS
    // =========================

    /** Lógica local para no depender de CartService y evitar ciclo de beans. */
    private Cart getOrCreateCartByUserId(Long userId) {
        return cartRepository.findByUserId(userId).orElseGet(() -> {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + userId));
            if (!"BUYER".equals(user.getRole().name())) {
                throw new RuntimeException("Solo los usuarios BUYER pueden tener carrito de compras");
            }
            Cart newCart = new Cart();
            newCart.setUser(user);
            return cartRepository.save(newCart);
        });
    }

    private String generateOrderNumber() {
        String today = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE); // yyyyMMdd
        String rand8 = UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
        return "PP-" + today + "-" + rand8;
    }

    private String buildItemTitle(Listing listing) {
        if (listing == null)
            return "Item";
        try {
            var v = listing.getVariant();
            if (v != null) {
                String s = v.toString();
                if (s != null && !s.isBlank())
                    return s;
            }
        } catch (Exception ignored) {
        }
        return "Listing #" + listing.getId();
    }
}
