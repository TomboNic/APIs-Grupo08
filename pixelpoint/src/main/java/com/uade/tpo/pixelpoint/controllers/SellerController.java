package com.uade.tpo.pixelpoint.controllers;

import java.net.URI;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
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

import com.uade.tpo.pixelpoint.entity.dto.SellerRequest;
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
    @PreAuthorize("hasAnyRole('ADMIN','SELLER')")
    @PostMapping
    public ResponseEntity<Seller> createSeller(
            @RequestBody SellerRequest request,
            Authentication auth) {

        String email = auth.getName(); // email del usuario logueado

        Seller created = sellerService.createSeller(
                email,
                request.getShopName().trim(),
                request.getDescription() != null ? request.getDescription().trim() : ""
        );

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getId())
                .toUri();

        return ResponseEntity.created(location).body(created);
    }

    // PUT /seller/{id} - Actualizar seller existente
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','SELLER')")
    public ResponseEntity<Seller> updateSeller(@PathVariable Long id, @RequestBody SellerRequest request) {
        if (request == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        Optional<Seller> sellerOpt = sellerService.getSellerById(id);
        if (sellerOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Seller seller = sellerOpt.get();
        if (request.getShopName() != null && !request.getShopName().trim().isEmpty()) {
            seller.setShopName(request.getShopName().trim());
        }
        if (request.getDescription() != null) {
            seller.setDescription(request.getDescription().trim());
        }
        
        Seller updated = sellerService.updateSeller(seller);
        return ResponseEntity.ok(updated);
    }

    // DELETE /seller/{id} - Eliminar seller
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','SELLER')")
    public ResponseEntity<Void> deleteSeller(@PathVariable Long id) {
        Optional<Seller> sellerOpt = sellerService.getSellerById(id);
        if (sellerOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        sellerService.deleteSeller(id);
        return ResponseEntity.noContent().build();
    }

}
