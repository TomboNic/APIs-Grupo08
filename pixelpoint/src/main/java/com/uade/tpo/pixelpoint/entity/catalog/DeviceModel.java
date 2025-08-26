package com.uade.tpo.pixelpoint.entity.catalog;

import jakarta.persistence.Entity;
import lombok.Data;

@Data
@Entity
public class DeviceModel {
    private Long id;
    private Brand brand;
    private String modelName;
}
