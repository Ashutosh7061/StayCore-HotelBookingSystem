package com.ashutosh.HotelBookingSystem.controller;

import com.ashutosh.HotelBookingSystem.dto.BookingResponseDTO;
import com.ashutosh.HotelBookingSystem.dto.BookingSummaryDTO;
import com.ashutosh.HotelBookingSystem.dto.HotelBookingResponseDTO;
import com.ashutosh.HotelBookingSystem.dto.UserBookingResponseDTO;
import com.ashutosh.HotelBookingSystem.entity.Booking;
import com.ashutosh.HotelBookingSystem.service.BookingService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingResponseDTO bookRoom(@RequestParam Long userId, @RequestParam Long hotelId,
                                       @RequestParam String roomType, @RequestParam int days,
                                       @RequestParam int rooms
                            ){
        return bookingService.bookRoom(userId, hotelId, roomType, days, rooms);
    }

    @GetMapping("/user/{userId}")
    public List<UserBookingResponseDTO> getUserBookings(@PathVariable Long userId){
        return bookingService.getUserBookings(userId);
    }

    @GetMapping("/{bookingId}/summary")
    public BookingSummaryDTO getBookingSummary(@PathVariable Long bookingId){
        return bookingService.getBookingSummary(bookingId);
    }

}
