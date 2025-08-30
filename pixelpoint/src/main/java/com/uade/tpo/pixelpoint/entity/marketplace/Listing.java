package com.uade.tpo.pixelpoint.entity.marketplace;

import com.uade.tpo.pixelpoint.entity.catalog.Variants;

import io.micrometer.common.lang.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Data
@Entity
public class Listing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "variant_id", nullable = false)
    private Variants variant;

    @ManyToOne
    @JoinColumn(name = "seller_id", nullable = false)
    private Seller seller;

    @Column
    private Float price;
    
    @Column
    private int stock;
    
    @Column
    private Boolean active;
    
    @Nullable
    private String version;
}
