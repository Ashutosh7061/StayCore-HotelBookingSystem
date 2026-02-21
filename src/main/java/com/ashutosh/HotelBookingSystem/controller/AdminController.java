package com.ashutosh.HotelBookingSystem.controller;

import com.ashutosh.HotelBookingSystem.Enum.BookingStatus;
import com.ashutosh.HotelBookingSystem.dto.AdminUserDetailsDTO;
import com.ashutosh.HotelBookingSystem.dto.HotelResponseDTO;
import com.ashutosh.HotelBookingSystem.dto.UserHotelResponseDTO;
import com.ashutosh.HotelBookingSystem.entity.Hotel;
import com.ashutosh.HotelBookingSystem.entity.User;
import com.ashutosh.HotelBookingSystem.service.AdminService;
import com.ashutosh.HotelBookingSystem.service.HotelService;
import com.ashutosh.HotelBookingSystem.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final HotelService hotelService;
    private final UserService userService;
    private final AdminService adminService;

    @GetMapping("/users")
    public List<AdminUserDetailsDTO> getAllUsers(){
        return adminService.getAllUsersForAdmin();
    }

    @GetMapping("/hotels")
    public List<HotelResponseDTO> getAllHotels(){
        return hotelService.getAllHotels();
    }

    @GetMapping("/hotel/{hotelId}")
    public Hotel getHotelWithSpecificId(@PathVariable Long hotelId){
        return hotelService.getHotelWithId(hotelId);
    }

    @GetMapping("/hotel/{hotelId}/user")
    public ResponseEntity<List<UserHotelResponseDTO>> getAllUserOfHotelByStatus(
            @PathVariable Long hotelId, @RequestParam(required = false) BookingStatus status){
        return ResponseEntity.ok(hotelService.getAllUserOfHotelByStatus(hotelId,status));
    }
}
