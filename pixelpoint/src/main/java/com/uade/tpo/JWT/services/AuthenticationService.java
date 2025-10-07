package com.uade.tpo.JWT.services;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.uade.tpo.JWT.dto.AuthenticationRequest;
import com.uade.tpo.JWT.dto.AuthenticationResponse;
import com.uade.tpo.pixelpoint.entity.dto.RegisterRequest;
import com.uade.tpo.pixelpoint.entity.marketplace.*;
import com.uade.tpo.pixelpoint.repository.marketplace.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegisterRequest req) {
        Role requested = req.getRole() != null ? req.getRole() : Role.BUYER;

        User user = new User();
        user.setFirstName(req.getFirstname()); // ojo con el nombre del campo en el DTO
        user.setLastName(req.getLastname());
        user.setEmail(req.getEmail());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setRole(requested);

        repository.save(user);

        String jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
        .token(jwtToken)
        .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest req) {
        // Autenticar credenciales
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword())
        );

        User user = repository.findByEmail(req.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        String jwtToken = jwtService.generateToken(user);

        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }
}
