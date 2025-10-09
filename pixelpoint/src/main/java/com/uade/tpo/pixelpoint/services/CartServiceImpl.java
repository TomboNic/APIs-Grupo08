package com.uade.tpo.pixelpoint.services;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uade.tpo.pixelpoint.entity.cart.Cart;
import com.uade.tpo.pixelpoint.entity.cart.CartItem;
import com.uade.tpo.pixelpoint.entity.cart.Order;
import com.uade.tpo.pixelpoint.entity.dto.CartItemResponse;
import com.uade.tpo.pixelpoint.entity.dto.CartResponse;
import com.uade.tpo.pixelpoint.entity.marketplace.Listing;
import com.uade.tpo.pixelpoint.entity.marketplace.User;
import com.uade.tpo.pixelpoint.repository.cart.CartItemsRepository;
import com.uade.tpo.pixelpoint.repository.cart.CartRepository;
import com.uade.tpo.pixelpoint.repository.marketplace.ListingRepository;
import com.uade.tpo.pixelpoint.repository.marketplace.UserRepository;

@Service
@Transactional
public class CartServiceImpl implements CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemsRepository cartItemsRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ListingRepository listingRepository;

    @Autowired
    private OrderService orderService;

    @Override
    public Cart getOrCreateCartByUserId(Long userId) {
        validateUserCanHaveCart(userId);

        // Buscar carrito existente
        Optional<Cart> existingCart = cartRepository.findByUserId(userId);

        if (existingCart.isPresent()) {
            return existingCart.get();
        }

        // Crear nuevo carrito
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + userId));

        Cart newCart = new Cart();
        newCart.setUser(user);

        return cartRepository.save(newCart);
    }

    @Override
    public Optional<Cart> getCartById(Long cartId) {
        return cartRepository.findById(cartId);
    }

    @Override
    public Page<Cart> getAllCarts(PageRequest pageRequest) {
        return cartRepository.findAll(pageRequest);
    }

    @Override
    public Cart addItemToCart(Long userId, Long listingId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a 0");
        }

        Cart cart = getOrCreateCartByUserId(userId);

        // Verificar que el listing existe
        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new RuntimeException("Listing no encontrado con ID: " + listingId));

        // Buscar si ya existe el item en el carrito
        Optional<CartItem> existingItem = cartItemsRepository.findByCartIdAndListingId(cart.getId(), listingId);

        if (existingItem.isPresent()) {
            // Actualizar cantidad existente
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + quantity);
            cartItemsRepository.save(item);
        } else {
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setListing(listing);
            newItem.setQuantity(quantity);
            newItem.setUnitPrice(listing.getEffectivePrice().doubleValue());
            cartItemsRepository.save(newItem);
        }
        return cartRepository.findById(cart.getId()).orElse(cart);
    }

    @Override
    public Cart updateItemQuantity(Long userId, Long listingId, int newQuantity) {
        if (newQuantity <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a 0");
        }

        Cart cart = getOrCreateCartByUserId(userId);

        CartItem item = cartItemsRepository.findByCartIdAndListingId(cart.getId(), listingId)
                .orElseThrow(() -> new RuntimeException("Item no encontrado en el carrito"));

        item.setQuantity(newQuantity);
        cartItemsRepository.save(item);

        return cartRepository.findById(cart.getId()).orElse(cart);
    }

    @Override
    public Cart removeItemFromCart(Long userId, Long listingId) {
        Cart cart = getOrCreateCartByUserId(userId);

        CartItem item = cartItemsRepository.findByCartIdAndListingId(cart.getId(), listingId)
                .orElseThrow(() -> new RuntimeException("Item no encontrado en el carrito"));

        cartItemsRepository.delete(item);

        return cartRepository.findById(cart.getId()).orElse(cart);
    }

    @Override
    public Cart clearCart(Long userId) {
        Cart cart = getOrCreateCartByUserId(userId);

        // Eliminar todos los items del carrito
        cartItemsRepository.deleteByCartId(cart.getId());

        return cartRepository.findById(cart.getId()).orElse(cart);
    }

    @Override
    public void deleteCart(Long cartId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Carrito no encontrado con ID: " + cartId));

        // Primero eliminar todos los items
        cartItemsRepository.deleteByCartId(cartId);

        // Luego eliminar el carrito
        cartRepository.delete(cart);
    }

    @Override
    public CartResponse convertToCartResponse(Cart cart) {
        CartResponse response = new CartResponse();
        response.setCartId(cart.getId());

        if (cart.getItems() != null) {
            for (CartItem item : cart.getItems()) {
                CartItemResponse itemResponse = new CartItemResponse();
                itemResponse.setItemId(item.getId());
                itemResponse.setListingId(item.getListing().getId());
                itemResponse.setQuantity(item.getQuantity());
                itemResponse.setUnitPrice(item.getUnitPrice());
                itemResponse.setSubtotal(item.getQuantity() * item.getUnitPrice());

                response.getItems().add(itemResponse);
            }
        }

        // Calcular total
        response.setTotal(BigDecimal.valueOf(calculateCartTotal(cart)));

        return response;
    }

    @Override
    public double calculateCartTotal(Cart cart) {
        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            return 0.0;
        }

        return cart.getItems().stream()
                .mapToDouble(item -> item.getQuantity() * item.getUnitPrice())
                .sum();
    }

    @Override
    public void validateUserCanHaveCart(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + userId));

        // Solo los BUYERS pueden tener carrito
        if (!user.getRole().name().equals("BUYER")) {
            throw new RuntimeException("Solo los usuarios BUYER pueden tener carrito de compras");
        }
    }

    @Override
    public Order checkout(Long userId) {
        return orderService.createFromCart(userId);
    }
    
}
