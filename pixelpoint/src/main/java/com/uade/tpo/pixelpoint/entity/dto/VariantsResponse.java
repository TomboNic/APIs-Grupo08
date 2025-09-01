package com.uade.tpo.pixelpoint.entity.dto;
import com.uade.tpo.pixelpoint.entity.catalog.Condition;
import lombok.Data;

@Data
public class VariantsResponse {
    private Long id;                // id propio de la variant
    private Long deviceModelId;     // id del DeviceModel asociado
    private String deviceModelName; // opcional, para mostrar nombre del modelo
    private int ram;
    private int storage;
    private String color;
    private Condition condition;
}
