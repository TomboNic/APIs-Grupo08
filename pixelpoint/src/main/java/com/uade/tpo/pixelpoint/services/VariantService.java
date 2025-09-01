package com.uade.tpo.pixelpoint.services;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.uade.tpo.pixelpoint.entity.catalog.Condition;
import com.uade.tpo.pixelpoint.entity.catalog.Variants;

public interface VariantService {

    Page<Variants> getVariants(PageRequest pageable);

    Optional<Variants> getVariantById(Long variantId);

    Variants createVariant(Long deviceModelId,
                           int ram,
                           int storage,
                           String color,
                           Condition condition);
}