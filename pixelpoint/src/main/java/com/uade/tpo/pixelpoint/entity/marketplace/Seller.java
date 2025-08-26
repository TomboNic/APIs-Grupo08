package com.uade.tpo.pixelpoint.entity.marketplace;

import jakarta.persistence.Entity;
import lombok.Data;

@Data
@Entity
public class Seller extends User{
    private Long id;
    private String displayName;
    private String shopDescription;
}
