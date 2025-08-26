package com.uade.tpo.pixelpoint.entity.marketplace;

import com.uade.tpo.pixelpoint.entity.catalog.Variants;

import io.micrometer.common.lang.Nullable;
import jakarta.persistence.Entity;
import lombok.Data;

@Data
@Entity
public class Listing {
    private Long id;
    private Variants variant;
    private Seller seller;
    private Float price;
    private int stock;
    private Boolean active;
    
    @Nullable
    private String version;
}
