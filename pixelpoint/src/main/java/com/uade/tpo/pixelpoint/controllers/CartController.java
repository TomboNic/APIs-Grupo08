package com.uade.tpo.pixelpoint.controllers;


import java.net.URI;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.uade.tpo.pixelpoint.entity.cart.Cart;
import com.uade.tpo.pixelpoint.repository.cart.CartRepository;


@RestController
@RequestMapping("carts")
public class CartController {

    @Autowired
    private CartRepository cartRepository;

    // GET /carts?page=0&size=20
    @GetMapping
    public ResponseEntity<Page<Cart>> getCarts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<Cart> carts = cartRepository.findAll(PageRequest.of(page, size));
        return ResponseEntity.ok(carts);
    }

    // GET /carts/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Cart> getCartById(@PathVariable Long id) {
        Optional<Cart> opt = cartRepository.findById(id);
        return opt.map(ResponseEntity::ok)
                  .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // POST /carts
    @PostMapping
    public ResponseEntity<Cart> createCart(@RequestBody(required = false) Cart body) {
        // Si te viene un body con algunos campos (p.ej. userId), se respetan.
        // Si viene null o vacío, se crea un Cart "vacío".
        Cart toCreate = (body == null) ? new Cart() : body;
        Cart created = cartRepository.save(toCreate);
        return ResponseEntity.created(URI.create("/carts/" + created.getId()))
                             .body(created);
    }

    // DELETE /carts/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCart(@PathVariable Long id) {
        if (!cartRepository.existsById(id)) return ResponseEntity.notFound().build();
        cartRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
