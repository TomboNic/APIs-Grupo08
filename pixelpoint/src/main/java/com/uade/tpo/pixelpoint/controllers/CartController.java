package com.uade.tpo.pixelpoint.controllers;


import java.net.URI;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
import com.uade.tpo.pixelpoint.entity.dto.CreateCartRequest;
import com.uade.tpo.pixelpoint.entity.dto.UpdateCartItemRequest;
import com.uade.tpo.pixelpoint.entity.marketplace.Listing;
import com.uade.tpo.pixelpoint.entity.marketplace.User;
import com.uade.tpo.pixelpoint.repository.cart.CartItemsRepository;
import com.uade.tpo.pixelpoint.repository.cart.CartRepository;
import com.uade.tpo.pixelpoint.repository.marketplace.ListingRepository;
import com.uade.tpo.pixelpoint.repository.marketplace.UserRepository;


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

    // GET /carts?page=0&size=20
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<Cart>> getCarts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<Cart> carts = cartRepository.findAll(PageRequest.of(page, size));
        return ResponseEntity.ok(carts);
    }

    // GET /carts/{id}
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','BUYER')")
    public ResponseEntity<Cart> getCartById(@PathVariable Long id) {
        Optional<Cart> opt = cartRepository.findById(id);
        return opt.map(ResponseEntity::ok)
                  .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // POST /carts
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','BUYER')")
    public ResponseEntity<Cart> createCart(@RequestBody CreateCartRequest request) {
        // Verificar que se proporcionó un userId
        if (request == null || request.getUserId() == null) {
            return ResponseEntity.badRequest().build();
        }

        // Buscar el usuario
        Optional<User> userOpt = userRepository.findById(request.getUserId());
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        User user = userOpt.get();
        
        // Crear el carrito
        Cart cart = new Cart();
        cart.setUser(user);
        
        Cart created = cartRepository.save(cart);
        return ResponseEntity.created(URI.create("/carts/" + created.getId()))
                             .body(created);
    }

    // POST /carts/{cartId}/items
    @PostMapping("/{cartId}/items")
    @PreAuthorize("hasAnyRole('ADMIN','BUYER')")
    public ResponseEntity<CartItem> addItemToCart(
            @PathVariable Long cartId, 
            @RequestBody AddCartItemRequest request) {
        
        logger.info("Adding item to cart. CartId: {}, Request: {}", cartId, request);
        
        // Validar que se proporcionaron los datos necesarios
        if (request == null || request.getListingId() == null || request.getQuantity() <= 0) {
            logger.warn("Invalid request: {}", request);
            return ResponseEntity.badRequest().build();
        }

        // Verificar que el carrito existe
        Optional<Cart> cartOpt = cartRepository.findById(cartId);
        if (cartOpt.isEmpty()) {
            logger.warn("Cart not found: {}", cartId);
            return ResponseEntity.notFound().build();
        }

        // Verificar que el listing existe
        Optional<Listing> listingOpt = listingRepository.findById(request.getListingId());
        if (listingOpt.isEmpty()) {
            logger.warn("Listing not found: {}", request.getListingId());
            return ResponseEntity.badRequest().build();
        }

        Cart cart = cartOpt.get();
        Listing listing = listingOpt.get();
        
        logger.info("Found cart: {} and listing: {}", cart.getId(), listing.getId());

        // Verificar si ya existe un item con este listing en el carrito
        Optional<CartItem> existingItemOpt = cartItemsRepository.findByCartIdAndListingId(cartId, request.getListingId());
        
        CartItem cartItem;
        if (existingItemOpt.isPresent()) {
            // Si ya existe, actualizar la cantidad
            logger.info("Updating existing cart item");
            cartItem = existingItemOpt.get();
            cartItem.setQuantity(cartItem.getQuantity() + request.getQuantity());
        } else {
            // Si no existe, crear nuevo item
            logger.info("Creating new cart item");
            cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setListing(listing);
            cartItem.setQuantity(request.getQuantity());
            cartItem.setUnitPrice(listing.getPrice()); // snapshot del precio actual
            logger.info("New cart item created with price: {}", listing.getPrice());
        }

        try {
            CartItem saved = cartItemsRepository.save(cartItem);
            logger.info("Cart item saved successfully: {}", saved.getId());
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            logger.error("Error saving cart item", e);
            return ResponseEntity.badRequest().build();
        }
    }

    // GET /carts/{cartId}/items
    @GetMapping("/{cartId}/items")
    @PreAuthorize("hasAnyRole('ADMIN','BUYER')")
    public ResponseEntity<List<CartItem>> getCartItems(@PathVariable Long cartId) {
        // Verificar que el carrito existe
        Optional<Cart> cartOpt = cartRepository.findById(cartId);
        if (cartOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Cart cart = cartOpt.get();
        return ResponseEntity.ok(cart.getItems());
    }

    // DELETE /carts/{cartId}/items/{itemId}
    @DeleteMapping("/{cartId}/items/{itemId}")
    @PreAuthorize("hasAnyRole('ADMIN','BUYER')")
    public ResponseEntity<Void> removeItemFromCart(
            @PathVariable Long cartId, 
            @PathVariable Long itemId) {
        
        // Verificar que el carrito existe
        if (!cartRepository.existsById(cartId)) {
            return ResponseEntity.notFound().build();
        }

        // Verificar que el item existe y pertenece al carrito
        Optional<CartItem> itemOpt = cartItemsRepository.findById(itemId);
        if (itemOpt.isEmpty() || !itemOpt.get().getCart().getId().equals(cartId)) {
            return ResponseEntity.notFound().build();
        }

        cartItemsRepository.deleteById(itemId);
        return ResponseEntity.noContent().build();
    }

    // PUT /carts/{cartId}/items/{itemId}
    @PutMapping("/{cartId}/items/{itemId}")
    @PreAuthorize("hasAnyRole('ADMIN','BUYER')")
    public ResponseEntity<CartItem> updateCartItem(
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
        return ResponseEntity.ok(updated);
    }

    // DELETE /carts/{id}
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCart(@PathVariable Long id) {
        if (!cartRepository.existsById(id)) return ResponseEntity.notFound().build();
        cartRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
