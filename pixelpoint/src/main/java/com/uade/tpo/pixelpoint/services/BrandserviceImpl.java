package com.uade.tpo.pixelpoint.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.uade.tpo.exceptions.BrandDuplicateException;
import com.uade.tpo.pixelpoint.entity.catalog.Brand;
import com.uade.tpo.pixelpoint.repository.catalog.BrandRepository;

@Service
public class BrandserviceImpl implements BrandService{

    @Autowired
    private BrandRepository brandRepository;

    @Override
    public Page<Brand> getBrands(PageRequest pageable) {
        return brandRepository.findAll(pageable);
    }

    @Override
    public Optional<Brand> getBrandById(Long brandId) {
        return brandRepository.findById(brandId);
    }

    @Override
    public Brand createBrand(String name) throws BrandDuplicateException {
        List<Brand> brands = brandRepository.findByNameIgnoreCase(name.trim());
        if (brands.isEmpty()) {
            Brand b = new Brand();
            b.setName(name.trim());
            return brandRepository.save(b);
        }
        throw new BrandDuplicateException();
    }
}
