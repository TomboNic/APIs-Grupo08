package com.uade.tpo.pixelpoint.controllers;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.uade.tpo.pixelpoint.entity.cart.Cart;
import com.uade.tpo.pixelpoint.entity.cart.CartItem;
import com.uade.tpo.pixelpoint.entity.dto.AddCartItemRequest;
import com.uade.tpo.pixelpoint.entity.dto.CartItemResponse;
import com.uade.tpo.pixelpoint.entity.dto.CartResponse;
import com.uade.tpo.pixelpoint.entity.dto.CreateCartRequest;
import com.uade.tpo.pixelpoint.entity.dto.UpdateCartItemRequest;
import com.uade.tpo.pixelpoint.entity.marketplace.Listing;
import com.uade.tpo.pixelpoint.entity.marketplace.User;
import com.uade.tpo.pixelpoint.repository.cart.CartItemsRepository;
import com.uade.tpo.pixelpoint.repository.cart.CartRepository;
import com.uade.tpo.pixelpoint.repository.marketplace.ListingRepository;
import com.uade.tpo.pixelpoint.repository.marketplace.UserRepository;
import com.uade.tpo.pixelpoint.services.CartService;

@RestController
@RequestMapping("carts")
public class CartController {

    private static final Logger logger = LoggerFactory.getLogger(CartController.class);

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartItemsRepository cartItemsRepository;

    @Autowired
    private ListingRepository listingRepository;

    @Autowired
    private CartService cartService;

    // GET /carts?page=0&size=20
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<CartResponse>> getCarts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<Cart> carts = cartRepository.findAll(PageRequest.of(page, size));
        Page<CartResponse> cartsResponse = carts.map(this::toCartResponse);
        return ResponseEntity.ok(cartsResponse);
    }

    // GET /carts/{id}
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','BUYER')")
    public ResponseEntity<CartResponse> getCartById(@PathVariable Long id) {
        Optional<Cart> opt = cartRepository.findById(id);
        if (opt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(toCartResponse(opt.get()));
    }

    // POST /carts
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','BUYER')")
    public ResponseEntity<CartResponse> createCart(@RequestBody CreateCartRequest request) {
        if (request == null || request.getUserId() == null) {
            return ResponseEntity.badRequest().build();
        }

        Optional<User> userOpt = userRepository.findById(request.getUserId());
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        Cart cart = new Cart();
        cart.setUser(userOpt.get());
        Cart created = cartRepository.save(cart);

        return ResponseEntity.created(URI.create("/carts/" + created.getId()))
                .body(toCartResponse(created)); // ✅ devuelve DTO sin datos sensibles
    }

    @PostMapping("/{cartId}/items")
    @PreAuthorize("hasAnyRole('ADMIN','BUYER')")
    public ResponseEntity<CartItemResponse> addItemToCart(
            @PathVariable Long cartId,
            @RequestBody AddCartItemRequest request) {

        logger.info("Adding item to cart. CartId: {}, Request: {}", cartId, request);

        // ✅ Validación (sin null-check para int)
        if (request == null || request.getListingId() == null || request.getQuantity() <= 0) {
            logger.warn("Invalid request: {}", request);
            return ResponseEntity.badRequest().build();
        }

        // ✅ Cargar cart y listing para validar existencia y obtener userId real
        Optional<Cart> cartOpt = cartRepository.findById(cartId);
        if (cartOpt.isEmpty()) {
            logger.warn("Cart not found: {}", cartId);
            return ResponseEntity.notFound().build();
        }
        Cart cart = cartOpt.get();

        Optional<Listing> listingOpt = listingRepository.findById(request.getListingId());
        if (listingOpt.isEmpty()) {
            logger.warn("Listing not found: {}", request.getListingId());
            return ResponseEntity.badRequest().build();
        }
        Listing listing = listingOpt.get();

        try {
            // ✅ Toda la lógica (incluida validación de stock) en el Service
            Long userId = cart.getUser().getId();
            cartService.addItemToCart(userId, request.getListingId(), request.getQuantity());

            // ✅ Leer el ítem desde la DB para responder (evita problemas de lazy)
            CartItem saved = cartItemsRepository
                    .findByCartIdAndListingId(cartId, request.getListingId())
                    .orElseThrow(() -> new IllegalStateException("Item not found after save"));

            logger.info("Cart item saved successfully: {}", saved.getId());
            return ResponseEntity.ok(toCartItemResponse(saved));

        } catch (IllegalArgumentException | IllegalStateException ex) {
            logger.warn("Business error adding item to cart: {}", ex.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception ex) {
            logger.error("Unexpected error adding item to cart", ex);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * ⚠️ IMPLEMENTAR según tu autenticación (JWT, SecurityContext, etc.)
     * Ejemplo temporal: devolver un ID fijo para pruebas.
     */
    private Long getCurrentUserId() {
        // Ejemplo temporal (reemplazar por tu método real)
        return 15L;
    }

    // GET /carts/{cartId}/items
    @GetMapping("/{cartId}/items")
    @PreAuthorize("hasAnyRole('ADMIN','BUYER')")
    public ResponseEntity<List<CartItemResponse>> getCartItems(@PathVariable Long cartId) {
        // Verificar que el carrito existe
        Optional<Cart> cartOpt = cartRepository.findById(cartId);
        if (cartOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Cart cart = cartOpt.get();
        List<CartItemResponse> items = cart.getItems().stream()
                .map(this::toCartItemResponse)
                .toList();
        return ResponseEntity.ok(items);
    }

    // DELETE /carts/{cartId}/items/{itemId}
    @DeleteMapping("/{cartId}/items/{itemId}")
    @PreAuthorize("hasAnyRole('ADMIN','BUYER')")
    public ResponseEntity<Map<String, String>> removeItemFromCart(
            @PathVariable Long cartId,
            @PathVariable Long itemId) {

        if (!cartRepository.existsById(cartId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Optional<CartItem> itemOpt = cartItemsRepository.findById(itemId);
        if (itemOpt.isEmpty() || !itemOpt.get().getCart().getId().equals(cartId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        cartItemsRepository.deleteById(itemId);
        return ResponseEntity.ok(Map.of("message", "Item eliminado del carrito"));
    }

    // PUT /carts/{cartId}/items/{itemId}
    @PutMapping("/{cartId}/items/{itemId}")
    @PreAuthorize("hasAnyRole('ADMIN','BUYER')")
    public ResponseEntity<CartItemResponse> updateCartItem(
            @PathVariable Long cartId,
            @PathVariable Long itemId,
            @RequestBody UpdateCartItemRequest request) {

        // Validar que se proporcionó una cantidad válida
        if (request == null || request.getQuantity() <= 0) {
            return ResponseEntity.badRequest().build();
        }

        // Verificar que el carrito existe
        if (!cartRepository.existsById(cartId)) {
            return ResponseEntity.notFound().build();
        }

        // Verificar que el item existe y pertenece al carrito
        Optional<CartItem> itemOpt = cartItemsRepository.findById(itemId);
        if (itemOpt.isEmpty() || !itemOpt.get().getCart().getId().equals(cartId)) {
            return ResponseEntity.notFound().build();
        }

        CartItem item = itemOpt.get();
        item.setQuantity(request.getQuantity());

        CartItem updated = cartItemsRepository.save(item);
        return ResponseEntity.ok(toCartItemResponse(updated));
    }

    /// DELETE /carts/{id}
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','BUYER')")
    public ResponseEntity<Map<String, String>> deleteCart(@PathVariable Long id) {
        if (!cartRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        cartRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Carrito vaciado correctamente"));
    }

    // Método helper para convertir CartItem a CartItemResponse
    private CartItemResponse toCartItemResponse(CartItem cartItem) {
        CartItemResponse response = new CartItemResponse();
        response.setItemId(cartItem.getId());
        response.setListingId(cartItem.getListing().getId());
        response.setQuantity(cartItem.getQuantity());
        response.setUnitPrice(cartItem.getUnitPrice());
        response.setSubtotal(cartItem.getUnitPrice() * cartItem.getQuantity());

        // Construir título del producto
        try {
            Listing listing = cartItem.getListing();
            if (listing != null && listing.getVariant() != null) {
                var variant = listing.getVariant();
                var deviceModel = variant.getDeviceModel();
                var brand = deviceModel.getBrand();

                String title = String.format("%s %s - %dGB RAM/%dGB - %s (%s)",
                        brand.getName(),
                        deviceModel.getModelName(),
                        variant.getRam(),
                        variant.getStorage(),
                        variant.getColor(),
                        variant.getCondition().toString());
                response.setTitle(title);
            } else {
                response.setTitle("Producto ID: " + cartItem.getListing().getId());
            }
        } catch (Exception e) {
            response.setTitle("Producto ID: " + cartItem.getListing().getId());
        }

        return response;
    }

    // Método helper para convertir Cart a CartResponse
    private CartResponse toCartResponse(Cart cart) {
        CartResponse response = new CartResponse();
        response.setCartId(cart.getId());

        // Convertir items a DTOs
        List<CartItemResponse> items = cart.getItems().stream()
                .map(this::toCartItemResponse)
                .toList();
        response.setItems(items);

        // Calcular total
        float total = (float) items.stream()
                .mapToDouble(CartItemResponse::getSubtotal)
                .sum();
        response.setTotal(new java.math.BigDecimal(total));

        return response;
    }
}
