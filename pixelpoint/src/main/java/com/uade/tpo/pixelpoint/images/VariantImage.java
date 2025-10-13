package com.uade.tpo.pixelpoint.images;

import java.time.Instant;

import com.uade.tpo.pixelpoint.entity.catalog.Variants;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "variant_images")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VariantImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación con la Variant
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "variant_id", nullable = false)
    private Variants variant;

    // Guardamos el binario en la DB (similar a tu ZIP de ejemplo que usa Blob)
    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "data", columnDefinition = "LONGBLOB", nullable = false)
    private byte[] data;

    // Metadatos útiles
    @Column(name = "content_type", length = 100, nullable = false)
    private String contentType;

    @Column(name = "filename", length = 255, nullable = false)
    private String filename;

    @Column(name = "size_bytes", nullable = false)
    private long sizeBytes;

    // Para indicar cuál mostrar por defecto
    @Column(name = "is_primary", nullable = false)
    private boolean primaryImage;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    void onCreate() {
        if (createdAt == null) createdAt = Instant.now();
    }
}
