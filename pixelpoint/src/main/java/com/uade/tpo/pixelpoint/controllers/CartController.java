package com.uade.tpo.pixelpoint.controllers;

// Spring Imports
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("cart")
public class CartController {

	// GET /cart
	@GetMapping
	public String getCart(@RequestParam(required = true) int cartId) {
		CartService cartService = new CartService();
        return cartService.getCartById(cartId);
	}

	// POST /cart
	@PostMapping
	public Brand postCart(@RequestBody String cartId) {
		CartService cartService = new CartService();
        return cartService.createCart(cartId);
	}
}
