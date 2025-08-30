package com.uade.tpo.pixelpoint.repository.catalog;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uade.tpo.pixelpoint.entity.catalog.DeviceModel;

public interface DeviceModelRepository extends JpaRepository<DeviceModel, Long>{
    List<DeviceModel> findByBrandId(Long brandId);
    boolean existsByBrandIdAndModelNameIgnoreCase(Long brandId, String modelName);
}
