package com.uade.tpo.pixelpoint.services;
import java.nio.file.AccessDeniedException;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.uade.tpo.pixelpoint.entity.dto.AuthenticationRequest;
import com.uade.tpo.pixelpoint.entity.dto.AuthenticationResponse;
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

    public AuthenticationResponse register(RegisterRequest req) throws AccessDeniedException {
        // Rol solicitado (si no viene, por defecto BUYER)
        Role requested = req.getRole() != null ? req.getRole() : Role.BUYER;

        // Solo ADMIN puede crear SELLER o ADMIN
        if (requested == Role.SELLER || requested == Role.ADMIN) {
            if (!currentUserHasAdminRole()) {
                throw new AccessDeniedException("Solo ADMIN puede crear ADMIN o SELLER");
            }
        }

        // Construcción del usuario
        User user = new User();
        user.setFirstName(req.getFirstname());
        user.setLastName(req.getLastname());
        user.setEmail(req.getEmail());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setRole(requested);

        // Persistir
        repository.save(user);

        // Generar JWT
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

        // Buscar usuario y generar token
        User user = repository.findByEmail(req.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        String jwtToken = jwtService.generateToken(user);

        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    private boolean currentUserHasAdminRole() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getAuthorities() == null) return false;
        return auth.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
    }
}
