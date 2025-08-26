package com.uade.tpo.pixelpoint.entity.marketplace;

import jakarta.persistence.Entity;
import lombok.Data;

@Data
@Entity
public class User {
    private Long id;
    private String username;
    private String email;
    private String password;
    private Role role;

}

