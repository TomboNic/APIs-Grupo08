package com.uade.tpo.pixelpoint.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uade.tpo.pixelpoint.entity.dto.UserResponse;
import com.uade.tpo.pixelpoint.services.UserServiceImpl;

import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

  @Autowired UserServiceImpl userService;

  @GetMapping("/me")
  public ResponseEntity<UserResponse> me(Authentication auth) {
    return ResponseEntity.ok(userService.findByEmail(auth.getName()));
  }
}
