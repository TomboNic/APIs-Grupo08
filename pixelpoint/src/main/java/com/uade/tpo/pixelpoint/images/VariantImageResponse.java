package com.uade.tpo.pixelpoint.images;

import lombok.Data;

@Data
public class VariantImageResponse {
    private Long id;
    private String filename;
    private String contentType;
    private long sizeBytes;
    private boolean primaryImage;
}