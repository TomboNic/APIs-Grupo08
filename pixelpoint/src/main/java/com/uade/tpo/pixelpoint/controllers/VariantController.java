package com.uade.tpo.pixelpoint.controllers;

import java.util.ArrayList;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("variant")
public class VariantController {

    @GetMapping()
    public ArrayList<Variant> getVariant() {
		VariantService variantService = new VariantService();
        return variantService.getDevice();
    }
    
    // Obtener por id
	@GetMapping("{variantId}") // GET - localhost:****/brand/3
	public Variant getDeviceMoedlById(@PathVariable Long variantId) {
		VariantService variantService = new VariantService();
        return variantService.getVariantById(variantId);
	}

    // Crear nueva Varianting
	@PostMapping
	public Variant postVariant(@RequestBody String variantId) {
		VariantService variantService = new VariantService();
        return variantService.createVariant(variantId);
	}

}
