package com.uade.tpo.pixelpoint.entity.marketplace;

import com.uade.tpo.pixelpoint.entity.catalog.Variants;

import io.micrometer.common.lang.Nullable;

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
