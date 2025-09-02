package com.uade.tpo.pixelpoint.entity.dto;

import lombok.Data;

@Data
public class SellerRequest {

    private Long userId;
    private String shopName;
    private String description;
    private String phone;
    private String logoUrl;

}
