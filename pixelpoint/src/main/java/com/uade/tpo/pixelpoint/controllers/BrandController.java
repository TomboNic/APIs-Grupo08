package com.uade.tpo.pixelpoint.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.uade.tpo.pixelpoint.entity.catalog.Brand;
import com.uade.tpo.pixelpoint.services.BrandService;

import jakarta.annotation.security.PermitAll;

import com.uade.tpo.exceptions.BrandDuplicateException;
import com.uade.tpo.pixelpoint.entity.dto.BrandRequest;
import com.uade.tpo.pixelpoint.repository.catalog.BrandRepository;

import java.net.URI;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("brands")
public class BrandController {
    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private BrandService brandService;

    @GetMapping
    @PermitAll
    public ResponseEntity<Page<Brand>> getBrands(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        if (page == null || size == null) {
            return ResponseEntity.ok(brandService.getBrands(PageRequest.of(0, Integer.MAX_VALUE)));
        }
        return ResponseEntity.ok(brandService.getBrands(PageRequest.of(page, size)));
    }

    @GetMapping("/{brandId}")
    @PermitAll
    public ResponseEntity<Brand> getBrandById(@PathVariable Long brandId) {
        Optional<Brand> result = brandService.getBrandById(brandId);
        if (result.isPresent()) {
            return ResponseEntity.ok(result.get());
        }
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Object> createBrand(@RequestBody BrandRequest brandRequest)
            throws BrandDuplicateException {
        Brand result = brandService.createBrand(brandRequest.getName());
        return ResponseEntity.created(URI.create("/brands/" + result.getId())).body(result);
    }

    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Brand> updateBrand(@PathVariable Long id, @RequestBody BrandRequest request) {
        Optional<Brand> opt = brandRepository.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        Brand b = opt.get();
        b.setName(request.getName().trim());
        Brand saved = brandRepository.save(b);
        return ResponseEntity.ok(saved); 
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteBrand(@PathVariable Long id) {
        if (!brandRepository.existsById(id)) return ResponseEntity.notFound().build();
        brandRepository.deleteById(id);
        return ResponseEntity.noContent().build(); 
    }
}