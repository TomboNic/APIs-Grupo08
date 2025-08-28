package com.uade.tpo.pixelpoint.entity.dto;

import lombok.Data;

@Data
public class AddItemRequest {
    private Long listingId;
    private Integer quantity;
}
