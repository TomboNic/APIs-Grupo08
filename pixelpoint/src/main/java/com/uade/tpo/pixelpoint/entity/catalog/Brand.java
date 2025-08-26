package com.uade.tpo.pixelpoint.entity.catalog;

import jakarta.persistence.Entity;
import lombok.Data;

@Data
@Entity
public class Brand {
    private Long id;
    private String name;
}
