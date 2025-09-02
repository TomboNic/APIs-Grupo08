package com.uade.tpo.pixelpoint.controllers;

import java.net.URI;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.uade.tpo.pixelpoint.entity.marketplace.Seller;
import com.uade.tpo.pixelpoint.services.SellerService;

@RestController
@RequestMapping("seller")
public class SellerController {

    @Autowired
    private SellerService sellerService;

    // GET /seller?page=0&size=20 - Listar sellers paginado
    @GetMapping
    public ResponseEntity<Page<Seller>> getSellers(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size) {
        int safePage = Math.max(0, page);
        int safeSize = Math.max(1, Math.min(100, size)); // Límite máximo de 100
        Page<Seller> sellers = sellerService.getSellers(PageRequest.of(safePage, safeSize));
        return ResponseEntity.ok(sellers);
    }

    // GET /seller/{id} - Obtener seller por ID
    @GetMapping("/{id}")
    public ResponseEntity<Seller> getSellerById(@PathVariable Long id) {
        Optional<Seller> sellerOpt = sellerService.getSellerById(id);
        return sellerOpt.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    // POST /seller - Crear nuevo seller
    @PostMapping
    public ResponseEntity<Seller> createSeller(@RequestBody SellerCreateRequest request) {
        if (request == null || 
            request.getDisplayName() == null || request.getDisplayName().trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        
        try {
            Seller created = sellerService.createSeller(
                request.getDisplayName().trim(), 
                request.getShopDescription() != null ? request.getShopDescription().trim() : ""
            );
            
            URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(created.getId())
                    .toUri();
            
            return ResponseEntity.created(location).body(created);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // PUT /seller/{id} - Actualizar seller existente
    @PutMapping("/{id}")
    public ResponseEntity<Seller> updateSeller(@PathVariable Long id, @RequestBody SellerUpdateRequest request) {
        if (request == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        Optional<Seller> sellerOpt = sellerService.getSellerById(id);
        if (sellerOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Seller seller = sellerOpt.get();
        if (request.getDisplayName() != null && !request.getDisplayName().trim().isEmpty()) {
            seller.setDisplayName(request.getDisplayName().trim());
        }
        if (request.getShopDescription() != null) {
            seller.setShopDescription(request.getShopDescription().trim());
        }

        Seller updated = sellerService.updateSeller(seller);
        return ResponseEntity.ok(updated);
    }

    // DELETE /seller/{id} - Eliminar seller
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSeller(@PathVariable Long id) {
        Optional<Seller> sellerOpt = sellerService.getSellerById(id);
        if (sellerOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        sellerService.deleteSeller(id);
        return ResponseEntity.noContent().build();
    }

    // Clases internas para requests
    public static class SellerCreateRequest {
        private String displayName;
        private String shopDescription;

        public String getDisplayName() { return displayName; }
        public void setDisplayName(String displayName) { this.displayName = displayName; }
        
        public String getShopDescription() { return shopDescription; }
        public void setShopDescription(String shopDescription) { this.shopDescription = shopDescription; }
    }

    public static class SellerUpdateRequest {
        private String displayName;
        private String shopDescription;

        public String getDisplayName() { return displayName; }
        public void setDisplayName(String displayName) { this.displayName = displayName; }
        
        public String getShopDescription() { return shopDescription; }
        public void setShopDescription(String shopDescription) { this.shopDescription = shopDescription; }
    }
}
