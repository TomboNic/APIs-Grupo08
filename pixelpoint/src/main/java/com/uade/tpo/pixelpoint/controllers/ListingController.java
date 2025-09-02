package com.uade.tpo.pixelpoint.controllers;

import java.net.URI;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.uade.tpo.pixelpoint.entity.marketplace.Listing;
import com.uade.tpo.pixelpoint.entity.dto.ListingRequest;
import com.uade.tpo.pixelpoint.entity.dto.ListingResponse;
import com.uade.tpo.pixelpoint.services.ListingService;


@RestController
@RequestMapping("listings")
public class ListingController {

    @Autowired
    private ListingService listingService;

    @GetMapping
    public ResponseEntity<Page<ListingResponse>> getCatalog(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<Listing> listings = listingService.catalog(PageRequest.of(page, size));
        Page<ListingResponse> body = listings.map(this::toResponse);
        return ResponseEntity.ok(body);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ListingResponse> getById(@PathVariable Long id) {
        Optional<Listing> opt = listingService.getById(id);
        return opt.map(l -> ResponseEntity.ok(toResponse(l)))
                  .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ListingResponse> create(@RequestBody ListingRequest req) {
        Listing created = listingService.create(
                req.getSellerId(),
                req.getVariantId(),
                req.getPrice(),
                req.getStock(),
                req.getActive()
        );
        return ResponseEntity
                .created(URI.create("/listings/" + created.getId()))
                .body(toResponse(created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ListingResponse> update(@PathVariable Long id, @RequestBody ListingRequest req) {
        Listing updated = listingService.update(
                id,
                req.getPrice(),
                req.getStock(),
                req.getActive(),
                req.getSellerId()
        );
        return ResponseEntity.ok(toResponse(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id,
                                       @RequestParam Long sellerId) {
        listingService.delete(id, sellerId);
        return ResponseEntity.noContent().build();
    }

    private ListingResponse toResponse(Listing l) {
        ListingResponse r = new ListingResponse();
        r.setId(l.getId());
        r.setPrice(l.getPrice());
        r.setStock(l.getStock());
        r.setActive(l.getActive());

        try { r.setSellerId(l.getSeller().getId()); } catch (Exception ignored) {}
        try { r.setVariantId(l.getVariant().getId()); } catch (Exception ignored) {}

        return r;
    }
}