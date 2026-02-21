package com.ashutosh.HotelBookingSystem.controller;


import com.ashutosh.HotelBookingSystem.dto.HotelBookingResponseDTO;
import com.ashutosh.HotelBookingSystem.dto.HotelResponseDTO;
import com.ashutosh.HotelBookingSystem.entity.Hotel;
import com.ashutosh.HotelBookingSystem.service.BookingService;
import com.ashutosh.HotelBookingSystem.service.HotelService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/hotels")
@RequiredArgsConstructor
public class HotelController {

    private final HotelService hotelService;
    private final BookingService bookingService;

    @PostMapping("/registerHotel")
    public Hotel registerHotel(@RequestBody Hotel hotel){
        return hotelService.registerHotel(hotel);
    }

    @GetMapping
    public List<HotelResponseDTO> getAllHotels(){
        return hotelService.getAllHotels();
    }

    @GetMapping("/{hotelId}/bookings")
    public List<HotelBookingResponseDTO> getHotelBooking(@PathVariable Long hotelId){
        return bookingService.getHotelBookings(hotelId);
    }

    @PutMapping("/{bookingId}/checkout")
    public String checkout(@PathVariable Long bookingId){
        return bookingService.checkout((bookingId));
    }
}
