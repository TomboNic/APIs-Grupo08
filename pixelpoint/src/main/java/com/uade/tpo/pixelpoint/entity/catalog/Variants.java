package com.uade.tpo.pixelpoint.entity.catalog;

import lombok.Builder;

@Builder
public class Variants {
    private Long id;
    private DeviceModel deviceModel;
    private int ram;
    private int storage;
    private String color;
    private Condition condition;
}
