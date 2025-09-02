package com.uade.tpo.pixelpoint.entity.dto;

import com.uade.tpo.pixelpoint.entity.cart.Status;

import lombok.Data;

@Data
public class CartRequest {
    private Long userId;
    private Status status;
}
