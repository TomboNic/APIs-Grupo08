package com.uade.tpo.pixelpoint.entity.cart;
import com.uade.tpo.pixelpoint.entity.marketplace.User;

import jakarta.persistence.Entity;
import lombok.Data;

@Data
@Entity
public class Cart {
    private Long id;
    private User user; //Solo buyer, seller no puede comprar
}
