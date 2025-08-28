package com.uade.tpo.pixelpoint.entity.dto;
import lombok.Data;

@Data
public class ListingImageResponse {
    private Long id;
    private Long listingId;
    private String url;
    private Integer sortOrder;
}
