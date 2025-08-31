package com.uade.tpo.pixelpoint.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.uade.tpo.pixelpoint.entity.catalog.DeviceModel;
import com.uade.tpo.pixelpoint.repository.catalog.DeviceModelRepository;
import com.uade.tpo.pixelpoint.repository.catalog.VariantsRepository;
import com.uade.tpo.pixelpoint.entity.catalog.Variants;
import com.uade.tpo.pixelpoint.entity.catalog.Condition;


public class VariantServiceImpl implements VariantService {
    
    @Autowired
    private VariantsRepository variantRepository;

    @Autowired
    private DeviceModelRepository deviceModelRepository;

    @Override
    public List<Variants> listByModel(Long modelId) {
        return variantRepository.findByDeviceModelId(modelId);
    }

    @Override
    public Variants createIfMissing(Long modelId,
                                   Integer ramGb,
                                   Integer storageGb,
                                   String color,
                                   Condition condition) {
        return variantRepository
                .findByDeviceModelIdAndRamAndStorageAndColorIgnoreCaseAndCondition(
                        modelId, ramGb, storageGb, color, condition
                )
                .orElseGet(() -> {
                    DeviceModel model = deviceModelRepository.findById(modelId)
                            .orElseThrow(() -> new IllegalArgumentException("DeviceModel no encontrado"));
                    Variants v = new Variants();
                    v.setDeviceModel(model);
                    v.setRam(ramGb);
                    v.setStorage(storageGb);
                    v.setColor(color.trim());
                    v.setCondition(condition);
                    return variantRepository.save(v);
                });
    }
}
