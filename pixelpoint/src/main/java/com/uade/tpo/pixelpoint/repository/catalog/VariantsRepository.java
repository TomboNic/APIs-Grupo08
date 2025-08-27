package com.uade.tpo.pixelpoint.repository.catalog;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uade.tpo.pixelpoint.entity.catalog.Variants;

public interface VariantsRepository extends JpaRepository<Variants, Long>{
    
}
