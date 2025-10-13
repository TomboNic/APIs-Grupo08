package com.uade.tpo.pixelpoint.images;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface VariantImageRepository extends JpaRepository<VariantImage, Long> {
    List<VariantImage> findByVariantIdOrderByPrimaryImageDescCreatedAtDesc(Long variantId);

    Optional<VariantImage> findFirstByVariantIdAndPrimaryImageTrue(Long variantId);

    long countByVariantId(Long variantId);

    // VariantImageRepository.java
    @Query("select vi from VariantImage vi where vi.primaryImage = true and vi.variant.id in :variantIds")
    List<VariantImage> findPrimariesByVariantIds(@Param("variantIds") List<Long> variantIds);
}
