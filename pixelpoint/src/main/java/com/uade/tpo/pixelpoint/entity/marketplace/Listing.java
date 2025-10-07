package com.uade.tpo.pixelpoint.entity.marketplace;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.uade.tpo.pixelpoint.entity.catalog.Variants;

import io.micrometer.common.lang.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

    @Enumerated(EnumType.STRING)
    @Column(name = "discount_type", nullable = false)
    private DiscountType discountType = DiscountType.NONE;

    @Column(name = "discount_value", nullable = false)
    private Double discountValue = 0.0;

    @Column(name = "discount_active", nullable = false)
    private Boolean discountActive = false;

    public enum DiscountType {
        NONE, PERCENT, AMOUNT
    }

    public BigDecimal getEffectivePrice() {
        BigDecimal base = BigDecimal.valueOf(price);
        if (Boolean.TRUE.equals(discountActive)) {
            switch (discountType) {
                case PERCENT -> base = base.multiply(BigDecimal.ONE.subtract(
                        BigDecimal.valueOf(discountValue).movePointLeft(2)));
                case AMOUNT -> base = base.subtract(BigDecimal.valueOf(discountValue));
                default -> {
                }
            }
        }
        if (base.signum() < 0)
            base = BigDecimal.ZERO;
        return base.setScale(2, RoundingMode.HALF_UP);
    }
}
