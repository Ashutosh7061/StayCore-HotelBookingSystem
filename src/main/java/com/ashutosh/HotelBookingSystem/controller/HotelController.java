package com.ashutosh.HotelBookingSystem.controller;


import com.ashutosh.HotelBookingSystem.entity.Hotel;
import com.ashutosh.HotelBookingSystem.service.HotelService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/hotels")
@RequiredArgsConstructor
public class HotelController {

    private final HotelService hotelService;

    @PostMapping("/registerHotel")
    public Hotel registerHotel(@RequestBody Hotel hotel){
        return hotelService.registerHotel(hotel);
    }

    @GetMapping
    public List<Hotel> getAllHotels(){
        return hotelService.getAllHotels();
    }
}
