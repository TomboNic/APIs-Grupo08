package com.uade.tpo.pixelpoint.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uade.tpo.pixelpoint.entity.dto.UpdateUserRequest;
import com.uade.tpo.pixelpoint.entity.dto.UserResponse;
import com.uade.tpo.pixelpoint.entity.marketplace.Role;
import com.uade.tpo.pixelpoint.entity.marketplace.User;
import com.uade.tpo.pixelpoint.entity.marketplace.Listing;
import com.uade.tpo.pixelpoint.entity.marketplace.Seller;
import com.uade.tpo.pixelpoint.repository.cart.CartItemsRepository;
import com.uade.tpo.pixelpoint.repository.cart.CartRepository;
import com.uade.tpo.pixelpoint.repository.cart.OrderRepository;
import com.uade.tpo.pixelpoint.repository.marketplace.ListingRepository;
import com.uade.tpo.pixelpoint.repository.marketplace.SellerRepository;
import com.uade.tpo.pixelpoint.repository.marketplace.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl {

  @Autowired
  UserRepository userRepository;

  @Autowired
  SellerRepository sellerRepository;

  @Autowired
  ListingRepository listingRepository;

  @Autowired
  CartRepository cartRepository;

  @Autowired
  CartItemsRepository cartItemsRepository;

  @Autowired
  OrderRepository orderRepository;

  public UserResponse findByEmail(String email) {
    var user = userRepository.findByEmail(email)
        .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

    return UserResponse.builder()
        .id(user.getId())
        .firstname(user.getFirstName())
        .lastname(user.getLastName())
        .email(user.getEmail())
        .roles(
            user.getAuthorities().stream()
                .map(a -> a.getAuthority().replaceFirst("^ROLE_", "")) // → BUYER, SELLER, ADMIN
                .collect(Collectors.toSet()))
        .build();
  }

  public UserResponse findById(Long id) {
    var user = userRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

    return UserResponse.builder()
        .id(user.getId())
        .firstname(user.getFirstName())
        .lastname(user.getLastName())
        .email(user.getEmail())
        .roles(
            user.getAuthorities().stream()
                .map(a -> a.getAuthority().replaceFirst("^ROLE_", "")) // → BUYER, SELLER, ADMIN
                .collect(Collectors.toSet()))
        .build();
  }

  public Page<UserResponse> getAllUsers(Pageable pageable) {
    Page<User> usersPage = userRepository.findAll(pageable);
    return usersPage.map(user -> UserResponse.builder()
        .id(user.getId())
        .firstname(user.getFirstName())
        .lastname(user.getLastName())
        .email(user.getEmail())
        .roles(
            user.getAuthorities().stream()
                .map(a -> a.getAuthority().replaceFirst("^ROLE_", ""))
                .collect(Collectors.toSet()))
        .build());
  }

  public List<UserResponse> getAllUsersList() {
    List<User> users = userRepository.findAll();
    return users.stream()
        .map(user -> UserResponse.builder()
            .id(user.getId())
            .firstname(user.getFirstName())
            .lastname(user.getLastName())
            .email(user.getEmail())
            .roles(
                user.getAuthorities().stream()
                    .map(a -> a.getAuthority().replaceFirst("^ROLE_", ""))
                    .collect(Collectors.toSet()))
            .build())
        .collect(Collectors.toList());
  }

  public UserResponse updateUser(Long id, UpdateUserRequest request) {
    User user = userRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

    // Actualizar solo los campos que vienen en el request
    if (request.getFirstname() != null && !request.getFirstname().isBlank()) {
      user.setFirstName(request.getFirstname());
    }
    if (request.getLastname() != null && !request.getLastname().isBlank()) {
      user.setLastName(request.getLastname());
    }
    if (request.getEmail() != null && !request.getEmail().isBlank()) {
      // Verificar que el email no esté en uso por otro usuario
      userRepository.findByEmail(request.getEmail()).ifPresent(existingUser -> {
        if (!existingUser.getId().equals(id)) {
          throw new RuntimeException("El email ya está en uso");
        }
      });
      user.setEmail(request.getEmail());
    }
    
    // Actualizar rol si viene en el request
    if (request.getRole() != null && !request.getRole().isBlank()) {
      try {
        Role newRole = Role.valueOf(request.getRole().toUpperCase());
        user.setRole(newRole);
      } catch (IllegalArgumentException e) {
        throw new RuntimeException("Rol inválido. Los roles válidos son: BUYER, SELLER, ADMIN");
      }
    }

    User updatedUser = userRepository.save(user);

    return UserResponse.builder()
        .id(updatedUser.getId())
        .firstname(updatedUser.getFirstName())
        .lastname(updatedUser.getLastName())
        .email(updatedUser.getEmail())
        .roles(
            updatedUser.getAuthorities().stream()
                .map(a -> a.getAuthority().replaceFirst("^ROLE_", ""))
                .collect(Collectors.toSet()))
        .build();
  }

  @Transactional
  public void deleteUser(Long id) {
    User user = userRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

    sellerRepository.findByUserId(id).ifPresent(seller -> removeSellerData(seller));

    cartRepository.findByUserId(id).ifPresent(cart -> {
      cartItemsRepository.deleteByCartId(cart.getId());
      cartRepository.delete(cart);
    });

    orderRepository.findByBuyerId(id).forEach(orderRepository::delete);

    userRepository.delete(user);
  }

  private void removeSellerData(Seller seller) {
    List<Listing> listings = listingRepository.findBySellerId(seller.getId());
    listings.forEach(listing -> {
      cartItemsRepository.deleteByListingId(listing.getId());
      listingRepository.delete(listing);
    });
    sellerRepository.delete(seller);
  }
}
