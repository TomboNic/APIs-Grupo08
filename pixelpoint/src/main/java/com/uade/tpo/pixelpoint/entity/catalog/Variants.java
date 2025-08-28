package com.uade.tpo.pixelpoint.entity.catalog;

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
public class Variants {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "device_model_id", nullable = false)
    private DeviceModel deviceModel;

    @Column
    private int ram;

    @Column
    private int storage;

    @Column
    private String color;

    @Enumerated(EnumType.STRING)
    @Column 
    private Condition condition;
}
