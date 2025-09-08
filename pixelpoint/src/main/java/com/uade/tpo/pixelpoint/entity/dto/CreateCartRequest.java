package com.uade.tpo.pixelpoint.entity.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class CreateCartRequest {
    @JsonProperty("user")
    private Long userId;
}