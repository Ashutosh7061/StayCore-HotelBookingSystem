package com.ashutosh.HotelBookingSystem.controller;

import com.ashutosh.HotelBookingSystem.entity.User;
import com.ashutosh.HotelBookingSystem.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/registerUser")
    public User registerUser(@RequestBody User user){
        return userService.registerUser(user);
    }

    @GetMapping("/login")
    public User loginUser(@RequestParam String email){
        return userService.loginUser(email);
    }
}
