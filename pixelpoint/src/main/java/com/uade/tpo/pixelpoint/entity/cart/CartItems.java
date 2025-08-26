package com.uade.tpo.pixelpoint.entity.cart;

import java.util.List;

import jakarta.persistence.Entity;
import lombok.Data;

@Data
@Entity
public class CartItems {
    private Long id;
    private Cart cart;
    private List<CartItems> cartItems;
    private int quantity;
    private int unitPrice; //Snapshot de precio al momento de agregar listing al carrito
}
