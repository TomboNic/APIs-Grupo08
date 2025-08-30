package com.uade.tpo.pixelpoint.services;

import java.util.List;

import com.uade.tpo.pixelpoint.entity.catalog.Condition;
import com.uade.tpo.pixelpoint.entity.catalog.Variants;

public interface VariantService {
    List<Variants> listByModel(Long modelId);

    Variants createIfMissing(Long modelId,
                            Integer ramGb,
                            Integer storageGb,
                            String color,
                            Condition condition);
}