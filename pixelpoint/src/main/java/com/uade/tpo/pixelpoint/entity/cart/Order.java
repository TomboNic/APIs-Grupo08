package com.uade.tpo.pixelpoint.entity.cart;

import com.uade.tpo.pixelpoint.entity.marketplace.User;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "orders")
public class Order {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private String orderNumber;

  @ManyToOne(optional = false)
  @JoinColumn(name = "buyer_id", nullable = false)
  private User buyer;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private OrderStatus status = OrderStatus.PAID;


  @Column(nullable = false) private Double subtotal;
  @Column(nullable = false) private Double discountTotal;
  @Column(nullable = false) private Double taxTotal;
  @Column(nullable = false) private Double grandTotal;

  @Column(nullable = false, updatable = false)
  private java.time.OffsetDateTime createdAt = java.time.OffsetDateTime.now();

  @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
  private java.util.List<OrderItem> items = new java.util.ArrayList<>();
}


