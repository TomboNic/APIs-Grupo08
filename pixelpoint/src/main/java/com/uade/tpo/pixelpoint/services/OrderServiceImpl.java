package com.uade.tpo.pixelpoint.services;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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

    @Override
    @Transactional
    public Order createFromCart(Long userId) {
        // 1) Obtener carrito del usuario
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalStateException("No se encontró el carrito del usuario " + userId));

        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new IllegalStateException("El carrito está vacío");
        }

        // 2) Crear orden base
        Order order = new Order();
        order.setBuyer(cart.getUser());
        order.setOrderNumber(generateOrderNumber());
        order.setStatus(OrderStatus.PENDING_PAYMENT);
        if (order.getItems() == null) {
            order.setItems(new java.util.ArrayList<>());
        }

        double subtotal = 0.0;

        for (CartItem ci : cart.getItems()) {
            Listing listing = ci.getListing();
            if (listing == null) {
                throw new IllegalStateException("Item de carrito sin listing asociado");
            }

            int qty = ci.getQuantity();
            if (qty <= 0) {
                throw new IllegalArgumentException("Cantidad inválida para el listing " + listing.getId());
            }

            int affected = listingRepository.decrementStock(listing.getId(), qty);
            if (affected == 0) {
                throw new IllegalStateException("Stock insuficiente para listing " + listing.getId());
            }

            double unit = 0.0;
            if (listing.getEffectivePrice() != null) {
                unit = listing.getEffectivePrice().doubleValue();
            } else if (listing.getPrice() != null) {
                unit = listing.getPrice().doubleValue();
            }

            OrderItem oi = new OrderItem();
            oi.setOrder(order);
            oi.setListingId(listing.getId());
            oi.setSellerId(listing.getSeller().getId());
            oi.setTitle(buildItemTitle(listing));
            oi.setUnitPrice(unit);
            oi.setQuantity(qty);
            oi.setLineTotal(unit * qty);

            order.getItems().add(oi);
            subtotal += oi.getLineTotal();
        }

        // 4) Totales
        order.setSubtotal(subtotal);
        order.setDiscountTotal(0.0);
        order.setTaxTotal(0.0);
        order.setGrandTotal(subtotal);

        // 5) Guardar orden
        Order saved = orderRepository.save(order);

        // 6) Vaciar carrito (conservar el carrito)
        cartItemsRepository.deleteByCartId(cart.getId());
        if (cart.getItems() != null) {
            cart.getItems().clear();
        }
        cartRepository.save(cart);

        // ✅ Asegurarse de retornar siempre un Order
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

        var v = listing.getVariant();
        if (v == null)
            return "Listing #" + listing.getId();

        // Brand y modelo (con defensivos por si alguna relación viene null)
        String brand = (v.getDeviceModel() != null && v.getDeviceModel().getBrand() != null)
                ? v.getDeviceModel().getBrand().getName()
                : null;

        String model = (v.getDeviceModel() != null)
                ? v.getDeviceModel().getModelName()
                : null;

        // Primitivos: usar > 0 si querés evitar mostrar "0GB"
        String ram = (v.getRam() > 0) ? (v.getRam() + "GB RAM") : null;
        String storage = (v.getStorage() > 0) ? (v.getStorage() + "GB") : null;

        // Strings: evitar vacíos
        String color = (v.getColor() != null && !v.getColor().isBlank()) ? v.getColor() : null;

        // Enum: mapear a etiqueta legible
        String conditionLabel = null;
        if (v.getCondition() != null) {
            switch (v.getCondition()) { // si es enum, podés switchar directo
                case NEW -> conditionLabel = "Nuevo";
                case REFURB -> conditionLabel = "Reacondicionado";
                case USED -> conditionLabel = "Usado";
                default -> conditionLabel = v.getCondition().name(); // fallback
            }
        }

        // Construir solo con partes no nulas/ vacías
        java.util.List<String> parts = new java.util.ArrayList<>();
        if (brand != null && !brand.isBlank())
            parts.add(brand);
        if (model != null && !model.isBlank())
            parts.add(model);
        if (ram != null)
            parts.add(ram);
        if (storage != null)
            parts.add(storage);
        if (color != null)
            parts.add(color);
        if (conditionLabel != null)
            parts.add(conditionLabel);

        if (parts.isEmpty())
            return "Listing #" + listing.getId();
        return String.join(" - ", parts);
    }

}
