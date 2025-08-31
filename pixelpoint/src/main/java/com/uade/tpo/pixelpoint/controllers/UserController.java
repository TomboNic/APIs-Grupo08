package com.uade.tpo.pixelpoint.controllers;

import java.util.ArrayList;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uade.tpo.pixelpoint.entity.marketplace.User;

@RestController
@RequestMapping("user")
public class UserController {

    @GetMapping()
    public ArrayList<User> getUser() {
		UserService userService = new UserService();
        return userService.getDevice();
    }
    
    // Obtener por id
	@GetMapping("{userId}") // GET - localhost:****/brand/3
	public User getDeviceMoedlById(@PathVariable Long userId) {
		UserService userService = new UserService();
        return userService.getUserById(userId);
	}

    // Crear nueva Usering
	@PostMapping
	public User postUser(@RequestBody String userId) {
		UserService userService = new UserService();
        return userService.createUser(userId);
	}

}
