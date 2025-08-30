package com.uade.tpo.pixelpoint.services;

import java.util.Optional;

import com.uade.tpo.exceptions.BrandDuplicateException;
import com.uade.tpo.pixelpoint.entity.catalog.Brand;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface BrandService {
    Page<Brand> getBrands(PageRequest pageRequest);
    Optional<Brand> getBrandById(Long brandId);
    Brand createBrand(String name) throws BrandDuplicateException;
}
