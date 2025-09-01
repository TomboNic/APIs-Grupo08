package com.uade.tpo.pixelpoint.entity.dto;

import com.uade.tpo.pixelpoint.entity.catalog.Condition;

import lombok.Data;

@Data
public class VariantsRequest {
    private Long deviceModelId;
    private int ram;
    private int storage;
    private String color;
    private Condition condition;

}
