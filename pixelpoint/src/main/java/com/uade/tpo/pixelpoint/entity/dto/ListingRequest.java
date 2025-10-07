package com.uade.tpo.pixelpoint.entity.dto;

import com.uade.tpo.pixelpoint.entity.marketplace.Listing.DiscountType;

import lombok.Data;

@Data
public class ListingRequest {
    private Long sellerId;
    private Long variantId;
    private float price;
    private int stock;
    private Boolean active;
    private DiscountType discountType = DiscountType.NONE;
    private Double discountValue = 0.0;
    private Boolean discountActive = false;
}
