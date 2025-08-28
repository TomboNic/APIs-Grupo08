package com.uade.tpo.pixelpoint.entity.dto;
import lombok.Data;

@Data
public class CartItemResponse {
    private Long itemId;
    private Long listingId;
    private String title;           // ej: "iPhone 13 128 Azul"
    private int quantity;
    private float unitPrice;
    private float subtotal;
}
