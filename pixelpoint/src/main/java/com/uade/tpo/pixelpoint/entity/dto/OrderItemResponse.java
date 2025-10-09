package com.uade.tpo.pixelpoint.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor @NoArgsConstructor @Builder
public class OrderItemResponse {
  private Long id;
  private Long listingId;
  private Long sellerId;
  private String title;
  private Double unitPrice;
  private Integer quantity;
  private Double lineTotal;
}
