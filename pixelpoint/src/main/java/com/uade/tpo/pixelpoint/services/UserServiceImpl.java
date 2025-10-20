package com.uade.tpo.pixelpoint.services;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uade.tpo.pixelpoint.entity.dto.UserResponse;
import com.uade.tpo.pixelpoint.repository.marketplace.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl {

  @Autowired
  UserRepository userRepository;

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
}
