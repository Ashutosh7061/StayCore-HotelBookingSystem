package com.ashutosh.HotelBookingSystem.controller;

import com.ashutosh.HotelBookingSystem.Enum.CancelledBy;
import com.ashutosh.HotelBookingSystem.dto.CancellationResponseDTO;
import com.ashutosh.HotelBookingSystem.entity.User;
import com.ashutosh.HotelBookingSystem.service.BookingService;
import com.ashutosh.HotelBookingSystem.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final BookingService bookingService;

    @PostMapping("/registerUser")
    public User registerUser(@RequestBody User user){
        return userService.registerUser(user);
    }

    @GetMapping("/login")
    public User loginUser(@RequestParam String email){
        return userService.loginUser(email);
    }

    @PutMapping("/{bookingId}/cancel")
    public CancellationResponseDTO cancelBooking(
            @PathVariable Long bookingId,
            @RequestParam CancelledBy cancelledBy,
            @RequestParam(required = true) String reason){

        return bookingService.cancelBooking(bookingId, cancelledBy, reason);
    }
}
