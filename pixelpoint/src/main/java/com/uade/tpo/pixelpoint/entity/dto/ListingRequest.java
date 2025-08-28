package com.uade.tpo.pixelpoint.entity.dto;

import lombok.Data;

@Data
public class ListingRequest {
    private Long variantId;
    private float price;
    private int stock;
    private Boolean active;
}
