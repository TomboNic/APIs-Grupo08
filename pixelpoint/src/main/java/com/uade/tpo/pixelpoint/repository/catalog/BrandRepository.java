package com.uade.tpo.pixelpoint.repository.catalog;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.uade.tpo.pixelpoint.entity.catalog.Brand;

public interface BrandRepository extends JpaRepository<Brand, Long>{

    List<Brand> findByNameIgnoreCase(String name);
    
}
