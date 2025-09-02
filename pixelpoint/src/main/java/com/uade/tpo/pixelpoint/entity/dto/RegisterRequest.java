package com.uade.tpo.pixelpoint.entity.dto;

import com.uade.tpo.pixelpoint.entity.marketplace.Role;
import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class RegisterRequest {
    private String firstname;
    private String lastname;
    private String email;
    private String password;
    private Role role; // BUYER | SELLER | ADMIN
}
