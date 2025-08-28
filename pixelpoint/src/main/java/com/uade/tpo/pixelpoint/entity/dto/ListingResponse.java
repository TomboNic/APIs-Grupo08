package com.uade.tpo.pixelpoint.entity.dto;
import lombok.Data;

@Data
public class ListingResponse {
    private Long id;
    private float price;
    private int stock;
    private Boolean active;
    private Long variantId;
    private Long sellerId;       // cuando tengamos usuarios
    //private List<String> images; // opcional
}
