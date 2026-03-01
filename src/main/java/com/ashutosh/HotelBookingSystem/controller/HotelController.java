package com.ashutosh.HotelBookingSystem.controller;


import com.ashutosh.HotelBookingSystem.dto.*;
import com.ashutosh.HotelBookingSystem.entity.Room;
import com.ashutosh.HotelBookingSystem.service.BookingService;
import com.ashutosh.HotelBookingSystem.service.HotelService;
import com.ashutosh.HotelBookingSystem.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/hotel")
@RequiredArgsConstructor
public class HotelController {

    private final HotelService hotelService;
    private final BookingService bookingService;
    private final RoomService roomService;


    @PostMapping("/{hotelId}/addRoom")
    public Room addRoom(@PathVariable Long hotelId, @RequestBody Room room){
        return roomService.addRoom(hotelId,room);
    }

//    @GetMapping
//    public List<HotelResponseDTO> getAllHotels(){
//        return hotelService.getAllHotels();
//    }

    @GetMapping("/{hotelId}/bookings")
    public List<HotelBookingResponseDTO> getIndividualHotelBooking(@PathVariable Long hotelId){
        return bookingService.getHotelBookings(hotelId);
    }

    @PutMapping("/bookings/{bookingId}/checkout")
    public CheckoutResponseDTO checkout(@PathVariable Long bookingId,
                                        @RequestParam(required = false) String review,
                                        @RequestParam(required = false) Integer rating,
                                        @RequestParam(required = false) String roomCondition){
        return hotelService.checkout(bookingId, review, rating, roomCondition);
    }

    @PostMapping("/check-in")
    public ResponseEntity<CheckInResponseDTO> checkIn(@RequestBody CheckInRequestDTO request){
        return ResponseEntity.ok(hotelService.checkIn(request));
    }
}
