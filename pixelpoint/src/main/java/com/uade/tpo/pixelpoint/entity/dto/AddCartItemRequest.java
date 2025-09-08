package com.uade.tpo.pixelpoint.entity.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class AddCartItemRequest {
    @JsonProperty("listing")
    private Long listingId;
    
    private int quantity;
}
