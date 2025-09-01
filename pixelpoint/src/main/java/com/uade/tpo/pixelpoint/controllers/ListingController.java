package com.uade.tpo.pixelpoint.controllers;

import java.util.ArrayList;

// Spring Imports
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uade.tpo.pixelpoint.entity.marketplace.Listing;
import com.uade.tpo.pixelpoint.services.ListingService;


@RestController
@RequestMapping("listing")
public class ListingController {
    
    // @GetMapping()
    // public ArrayList<Listing> getList() {
	// 	ListingService listingService = new ListingService();
    //     return listingService.catalog(Pageable);
    // }
    
    // // Obtener por id
	// @GetMapping("{listId}") // GET - localhost:****/brand/3
	// public Listing getDeviceMoedlById(@PathVariable Long listId) {
	// 	ListingService listService = new ListingService();
    //     return listService.getListingById(listId);
	// }

    // // Crear nueva Listinging
	// @PostMapping
	// public Listing postListing(@RequestBody String listId) {
	// 	ListingService listService = new ListingService();
    //     return listService.createListing(listId);
	// }
}
