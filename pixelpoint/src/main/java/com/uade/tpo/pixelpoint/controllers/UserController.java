package com.uade.tpo.pixelpoint.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.uade.tpo.pixelpoint.entity.dto.UpdateUserRequest;
import com.uade.tpo.pixelpoint.entity.dto.UserResponse;
import com.uade.tpo.pixelpoint.services.UserServiceImpl;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

  @Autowired
  UserServiceImpl userService;

  // GET /api/v1/users/me - Obtener mi perfil
  @GetMapping("/me")
  public ResponseEntity<UserResponse> me(Authentication auth) {
    return ResponseEntity.ok(userService.findByEmail(auth.getName()));
  }

  // GET /api/v1/users - Listar todos los usuarios (solo ADMIN)
  @GetMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Page<UserResponse>> getAllUsers(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size) {
    Page<UserResponse> users = userService.getAllUsers(PageRequest.of(page, size));
    return ResponseEntity.ok(users);
  }

  // GET /api/v1/users/{id} - Obtener usuario por ID (solo ADMIN)
  @GetMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
    try {
      UserResponse user = userService.findById(id);
      return ResponseEntity.ok(user);
    } catch (RuntimeException e) {
      return ResponseEntity.notFound().build();
    }
  }

  // PUT /api/v1/users/me - Actualizar mi perfil
  @PutMapping("/me")
  public ResponseEntity<UserResponse> updateMe(
      Authentication auth,
      @RequestBody UpdateUserRequest request) {
    try {
      // Obtener el ID del usuario autenticado
      UserResponse currentUser = userService.findByEmail(auth.getName());
      UserResponse updatedUser = userService.updateUser(currentUser.getId(), request);
      return ResponseEntity.ok(updatedUser);
    } catch (RuntimeException e) {
      return ResponseEntity.badRequest().build();
    }
  }

  // PUT /api/v1/users/{id} - Actualizar usuario por ID (solo ADMIN)
  @PutMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<UserResponse> updateUser(
      @PathVariable Long id,
      @RequestBody UpdateUserRequest request) {
    try {
      UserResponse updatedUser = userService.updateUser(id, request);
      return ResponseEntity.ok(updatedUser);
    } catch (RuntimeException e) {
      return ResponseEntity.badRequest().build();
    }
  }

  // DELETE /api/v1/users/{id} - Eliminar usuario (solo ADMIN)
  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
    try {
      userService.deleteUser(id);
      return ResponseEntity.noContent().build();
    } catch (RuntimeException e) {
      return ResponseEntity.notFound().build();
    }
  }
}
