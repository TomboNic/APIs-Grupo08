package com.uade.tpo.pixelpoint.JWT.configs;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.uade.tpo.pixelpoint.JWT.dto.AuthenticationRequest;
import com.uade.tpo.pixelpoint.JWT.dto.AuthenticationResponse;
import com.uade.tpo.pixelpoint.JWT.services.AuthenticationService;
import com.uade.tpo.pixelpoint.entity.dto.RegisterRequest;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService service;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(service.register(request));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest request) {
        return ResponseEntity.ok(service.authenticate(request));
    }
}
