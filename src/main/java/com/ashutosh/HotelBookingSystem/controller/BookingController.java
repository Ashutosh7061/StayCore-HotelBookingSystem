package com.ashutosh.HotelBookingSystem.controller;

import com.ashutosh.HotelBookingSystem.dto.BookingResponseDTO;
import com.ashutosh.HotelBookingSystem.dto.BookingSummaryDTO;
import com.ashutosh.HotelBookingSystem.dto.UserBookingResponseDTO;
import com.ashutosh.HotelBookingSystem.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingResponseDTO bookRoom(@RequestParam Long userId, @RequestParam Long hotelId,
                                       @RequestParam String roomType, @RequestParam int rooms,
                                       @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)LocalDate checkInDate,
                                       @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOutDate,
                                       @RequestParam(required = false) Boolean duplicateBooking
                                       ){
        return bookingService.bookRoom(userId, hotelId, roomType,checkInDate,checkOutDate, rooms, duplicateBooking);
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
