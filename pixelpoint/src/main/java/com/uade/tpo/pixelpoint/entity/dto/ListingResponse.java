package com.uade.tpo.pixelpoint.entity.dto;
import java.math.BigDecimal;

import com.uade.tpo.pixelpoint.entity.marketplace.Listing.DiscountType;

import lombok.Data;

@Data
public class ListingResponse {
    private Long id;
    private Float price;
    private int stock;
    private Boolean active;
    private Long variantId;
    private Long sellerId;
    private DiscountType discountType;
    private Double discountValue;
    private Boolean discountActive;
    private BigDecimal effectivePrice;
}
