package com.uade.tpo.pixelpoint.controllers;

import java.util.ArrayList;

// Spring Imports
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// Brand Complements Imports
import com.uade.tpo.pixelpoint.services.BrandService;
import com.uade.tpo.pixelpoint.entity.catalog.Brand;


@RestController
@RequestMapping("brand")
public class BrandController {

    // Obtener todas las marcas
    @GetMapping()
    public ArrayList<Brand> getMethodName() {
		BrandService brandService = new BrandService();
        return brandService.getBrands();
    }
    
    // Obtener por id
	@GetMapping("{brandId}") // GET - localhost:****/brand/3
	public Brand getBrandById(@PathVariable int brandId) {
		BrandService brandService = new BrandService();
        return brandService.getBrandById(brandId);
	}

    // Crear nueva Brand
	@PostMapping
	public Brand postBrand(@RequestBody String brandId) {
		BrandService brandService = new BrandService();
        return brandService.createBrand(brandId);
	}
}