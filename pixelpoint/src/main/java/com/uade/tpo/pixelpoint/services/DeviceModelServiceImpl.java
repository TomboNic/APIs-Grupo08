package com.uade.tpo.pixelpoint.services;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uade.tpo.exceptions.DeviceModelDuplicateException;
import com.uade.tpo.pixelpoint.entity.catalog.Brand;
import com.uade.tpo.pixelpoint.entity.catalog.DeviceModel;
import com.uade.tpo.pixelpoint.repository.catalog.BrandRepository;
import com.uade.tpo.pixelpoint.repository.catalog.DeviceModelRepository;

@Service
public class DeviceModelServiceImpl implements DeviceModelService {

    @Autowired
    private DeviceModelRepository modelRepository;

    @Autowired
    private BrandRepository brandRepository;

    @Override
    public List<DeviceModel> listByBrand(Long brandId) {
        return modelRepository.findByBrandId(brandId);
    }

    @Override
    public Optional<DeviceModel> getById(Long id) {
        return modelRepository.findById(id);
    }

    @Override
    public DeviceModel createModel(Long brandId, String modelName) throws DeviceModelDuplicateException {
        Brand brand = brandRepository.findById(brandId)
                .orElseThrow(() -> new NoSuchElementException("Brand no encontrada: " + brandId));

        if (modelRepository.existsByBrandIdAndModelNameIgnoreCase(brandId, modelName)) {
            throw new DeviceModelDuplicateException();
        }

        DeviceModel model = new DeviceModel();
        model.setBrand(brand);
        model.setModelName(modelName.trim());
        return modelRepository.save(model);
    }

    @Override
    public DeviceModel updateModel(Long id, String modelName) throws DeviceModelDuplicateException {
        DeviceModel model = modelRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Modelo no encontrado: " + id));

        if (!model.getModelName().equalsIgnoreCase(modelName)
                && modelRepository.existsByBrandIdAndModelNameIgnoreCase(model.getBrand().getId(), modelName)) {
            throw new DeviceModelDuplicateException();
        }

        model.setModelName(modelName.trim());
        return modelRepository.save(model);
    }

    @Override
    public void deleteModel(Long id) {
        modelRepository.deleteById(id);
    }
}
