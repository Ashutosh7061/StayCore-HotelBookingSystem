package com.ashutosh.HotelBookingSystem.controller;


import com.ashutosh.HotelBookingSystem.dto.*;
import com.ashutosh.HotelBookingSystem.entity.Room;
import com.ashutosh.HotelBookingSystem.security.CustomUserDetails;
import com.ashutosh.HotelBookingSystem.service.BookingService;
import com.ashutosh.HotelBookingSystem.service.HotelService;
import com.ashutosh.HotelBookingSystem.service.RoomService;
import com.ashutosh.HotelBookingSystem.service.SupportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/hotel")
@RequiredArgsConstructor
public class HotelController {

    private final HotelService hotelService;
    private final BookingService bookingService;
    private final RoomService roomService;
    private final SupportService supportService;


    @PreAuthorize("hasRole('HOTEL')")
    @PostMapping("/addRoom")
    public Room addRoom(@RequestBody Room room){
        return roomService.addRoom( room);
    }

    @PreAuthorize("hasRole('HOTEL')")
    @GetMapping("/bookings")
    public ResponseEntity<List<HotelBookingSummaryDTO>> getIndividualHotelBooking(){
        return ResponseEntity.ok(bookingService.getHotelBookings());
    }

    @PreAuthorize("hasRole('HOTEL')")
    @GetMapping("/{bookingId}/specific-booking")
    public ResponseEntity<HotelSpecificBookingDetailsDTO> getSpecificBookingDetails(@PathVariable Long bookingId){
        return ResponseEntity.ok(bookingService.getIndividualBookingDetails(bookingId));
    }

    @PreAuthorize("hasRole('HOTEL')")
    @PutMapping("/checkout")
    public ResponseEntity<CheckoutResponseDTO> checkout(@RequestBody CheckOutRequestDTO request){
        return ResponseEntity.ok(hotelService.checkout(request));
    }

    @PreAuthorize("hasRole('HOTEL')")
    @PostMapping("/check-in")
    public ResponseEntity<CheckInResponseDTO> checkIn(@RequestBody CheckInRequestDTO request){
        return ResponseEntity.ok(hotelService.checkIn(request));
    }

    @PreAuthorize("hasRole('HOTEL')")
    @PutMapping("/reapply")
    public String reapplyHotel(){
        return hotelService.reapplyHotel();
    }


}
