package com.uade.tpo.pixelpoint.controllers;

import java.util.ArrayList;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("seller")
public class SellerController {

    @GetMapping()
    public ArrayList<Seller> getSeller() {
		SellerService sellerService = new SellerService();
        return sellerService.getDevice();
    }
    
    // Obtener por id
	@GetMapping("{sellerId}") // GET - localhost:****/brand/3
	public Seller getDeviceMoedlById(@PathVariable Long sellerId) {
		SellerService sellerService = new SellerService();
        return sellerService.getSellerById(sellerId);
	}

    // Crear nueva Sellering
	@PostMapping
	public Seller postSeller(@RequestBody String sellerId) {
		SellerService sellerService = new SellerService();
        return sellerService.createSeller(sellerId);
	}
}
