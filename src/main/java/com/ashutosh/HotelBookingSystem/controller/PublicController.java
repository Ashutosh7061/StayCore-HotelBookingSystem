package com.ashutosh.HotelBookingSystem.controller;

import com.ashutosh.HotelBookingSystem.dto.*;
import com.ashutosh.HotelBookingSystem.entity.Hotel;
import com.ashutosh.HotelBookingSystem.entity.User;
import com.ashutosh.HotelBookingSystem.service.AuthService;
import com.ashutosh.HotelBookingSystem.service.HotelService;
import com.ashutosh.HotelBookingSystem.service.RoomService;
import com.ashutosh.HotelBookingSystem.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/public")
@RequiredArgsConstructor
public class PublicController {

    private final UserService userService;
    private final HotelService hotelService;
    private final RoomService roomService;
    private final AuthService authService;

//    @PostMapping("/registerUser")
//    public User registerUser(@RequestBody User user){
//        return userService.registerUser(user);
//    }
     @PostMapping("/registerUser")
     public String registerUser(@RequestBody UserSignupRequestDTO request){
        return authService.registerUser(request);
     }

    @PostMapping("/registerHotel")
    public String registerHotel(@RequestBody HotelSignupRequestDTO hotel){
        return authService.registerHotel(hotel);
    }

    @PostMapping("/login")
    public LoginResponseDTO loginUser(@RequestBody LoginRequestDTO request){
        return authService.login(request);
    }

    @GetMapping("/hotel/{hotelId}/allRooms")
    public List<AdminRoomDTO> getRooms(@PathVariable Long hotelId){
        return roomService.getRoomsByHotel(hotelId);
    }

}
