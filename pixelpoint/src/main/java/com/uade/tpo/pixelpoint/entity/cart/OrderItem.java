package com.uade.tpo.pixelpoint.entity.cart;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data
public class OrderItem {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false)
  @JoinColumn(name = "order_id", nullable = false)
  private Order order;

  @Column(nullable = false) private Long listingId;
  @Column(nullable = false) private Long sellerId;
  @Column(nullable = false, length = 255) private String title;

  @Column(nullable = false) private Double unitPrice; 
  @Column(nullable = false) private Integer quantity;
  @Column(nullable = false) private Double lineTotal; 
}
