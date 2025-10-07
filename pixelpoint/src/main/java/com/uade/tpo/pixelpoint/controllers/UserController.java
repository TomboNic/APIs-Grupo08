package com.uade.tpo.pixelpoint.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("user")
public class UserController {

    // @GetMapping()
    // public ArrayList<User> getUser() {
	// 	UserService userService = new UserService();
    //     return userService.getDevice();
    // }
    
    // // Obtener por id
	// @GetMapping("{userId}") // GET - localhost:****/brand/3
	// public User getDeviceMoedlById(@PathVariable Long userId) {
	// 	UserService userService = new UserService();
    //     return userService.getUserById(userId);
	// }

    // // Crear nueva Usering
	// @PostMapping
	// public User postUser(@RequestBody String userId) {
	// 	UserService userService = new UserService();
    //     return userService.createUser(userId);
	// }

}
