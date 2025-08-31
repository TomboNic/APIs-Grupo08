package com.uade.tpo.pixelpoint.repository.catalog;
import com.uade.tpo.pixelpoint.entity.catalog.Condition;
import com.uade.tpo.pixelpoint.entity.catalog.Variants;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;


public interface VariantsRepository extends JpaRepository<Variants, Long>{
    List<Variants> findByDeviceModelId(Long modelId);

    Optional<Variants> findByDeviceModelIdAndRamAndStorageAndColorIgnoreCaseAndCondition(
        Long deviceModelId,
        Integer ram,
        Integer storage,
        String color,
        Condition condition
    );
}
