package com.ashutosh.HotelBookingSystem.controller;

import com.ashutosh.HotelBookingSystem.Enum.BookingStatus;
import com.ashutosh.HotelBookingSystem.dto.*;
import com.ashutosh.HotelBookingSystem.entity.Hotel;
import com.ashutosh.HotelBookingSystem.service.AdminService;
import com.ashutosh.HotelBookingSystem.service.BookingService;
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
    private final AdminService adminService;
    private final BookingService bookingService;

    @GetMapping("/users")
    public List<AdminUserDetailsDTO> getAllUsers(){
        return adminService.getAllUsersForAdmin();
    }

    @GetMapping("/hotels")
    public List<HotelResponseDTO> getAllHotels(){
        return hotelService.getAllHotels();
    }

    @GetMapping("/hotel/{hotelId}")
    public ResponseEntity<AdminHotelDetailsDTO> getHotelWithSpecificId(@PathVariable Long hotelId){
        return ResponseEntity.ok(adminService.getAdminHotelDetails(hotelId));
    }

    @GetMapping("/hotel/{hotelId}/users")
    public ResponseEntity<List<BaseUserPerHotelResponseDTO>> getAllUserOfHotelByStatus(
            @PathVariable Long hotelId, @RequestParam(required = false) BookingStatus status){
        return ResponseEntity.ok(bookingService.getAllUserOfHotelByStatus(hotelId,status));
    }

    @GetMapping("/completed-bookings")
    public List<AdminBookingDetailsDTO> getCompletedBookings(){
        return bookingService.getCompletedBookingForAdmin();
    }

    @GetMapping("/dashboard")
    public PlatformDashboardDTO getDashBoard(){
        return adminService.getPlatformDashboard();
    }
}
