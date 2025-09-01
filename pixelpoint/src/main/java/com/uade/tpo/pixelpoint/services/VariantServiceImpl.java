package com.uade.tpo.pixelpoint.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.uade.tpo.pixelpoint.entity.catalog.DeviceModel;
import com.uade.tpo.pixelpoint.entity.catalog.Variants;
import com.uade.tpo.pixelpoint.entity.catalog.Condition;
import com.uade.tpo.pixelpoint.repository.catalog.DeviceModelRepository;
import com.uade.tpo.pixelpoint.repository.catalog.VariantsRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class VariantServiceImpl implements VariantService {

    @Autowired
    private VariantsRepository variantsRepository;

    @Autowired
    private DeviceModelRepository deviceModelRepository;

    @Override
    public Page<Variants> getVariants(PageRequest pageable) {
        return variantsRepository.findAll(pageable);
    }

    @Override
    public Optional<Variants> getVariantById(Long variantId) {
        return variantsRepository.findById(variantId);
    }

    @Override
    public Variants createVariant(Long deviceModelId,
                                  int ram,
                                  int storage,
                                  String color,
                                  Condition condition) {
        DeviceModel dm = deviceModelRepository.findById(deviceModelId)
                .orElseThrow(() -> new EntityNotFoundException("DeviceModel id=" + deviceModelId + " no existe"));

        Variants v = new Variants();
        v.setDeviceModel(dm);
        v.setRam(ram);
        v.setStorage(storage);
        v.setColor(color);
        v.setCondition(condition);

        return variantsRepository.save(v);
    }
}