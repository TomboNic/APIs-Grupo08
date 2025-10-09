package com.uade.tpo.pixelpoint.entity.dto;

import java.time.OffsetDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor @NoArgsConstructor @Builder
public class OrderResponse {
  private Long id;
  private String orderNumber;
  private Long buyerId;
  private String status;
  private Double subtotal;
  private Double discountTotal;
  private Double taxTotal;
  private Double grandTotal;
  private OffsetDateTime createdAt;
  private List<OrderItemResponse> items;
}
