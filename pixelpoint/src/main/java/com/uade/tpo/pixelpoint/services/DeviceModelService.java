package com.uade.tpo.pixelpoint.services;

import java.util.List;
import java.util.Optional;

import com.uade.tpo.exceptions.DeviceModelDuplicateException;
import com.uade.tpo.pixelpoint.entity.catalog.DeviceModel;

public interface DeviceModelService {
    List<DeviceModel> listByBrand(Long brandId);
    Optional<DeviceModel> getById(Long id);
    DeviceModel createModel(Long brandId, String modelName) throws DeviceModelDuplicateException;
    DeviceModel updateModel(Long id, String modelName) throws DeviceModelDuplicateException;
    void deleteModel(Long id);
}
