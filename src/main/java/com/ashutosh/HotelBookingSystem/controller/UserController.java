package com.ashutosh.HotelBookingSystem.controller;

import com.ashutosh.HotelBookingSystem.Enum.BookingStatus;
import com.ashutosh.HotelBookingSystem.Enum.CancelledBy;
import com.ashutosh.HotelBookingSystem.dto.BookingResponseDTO;
import com.ashutosh.HotelBookingSystem.dto.BookingSummaryDTO;
import com.ashutosh.HotelBookingSystem.dto.CancellationResponseDTO;
import com.ashutosh.HotelBookingSystem.dto.UserBookingResponseDTO;
import com.ashutosh.HotelBookingSystem.entity.User;
import com.ashutosh.HotelBookingSystem.service.BookingService;
import com.ashutosh.HotelBookingSystem.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final BookingService bookingService;

    @GetMapping("/login")
    public User loginUser(@RequestParam String email){
        return userService.loginUser(email);
    }

    @PostMapping
    public BookingResponseDTO bookRoom(@RequestParam Long userId, @RequestParam Long hotelId,
                                       @RequestParam String roomType, @RequestParam int rooms,
                                       @RequestParam @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate checkInDate,
                                       @RequestParam @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate checkOutDate,
                                       @RequestParam(required = false) Boolean duplicateBooking
    ){
        return bookingService.bookRoom(userId, hotelId, roomType,checkInDate,checkOutDate, rooms, duplicateBooking);
    }

    @GetMapping("/{userId}/bookings")
    public ResponseEntity<List<UserBookingResponseDTO>> getIndividualUserBookings(
            @PathVariable Long userId,
            @RequestParam(required = false) BookingStatus status){

        return ResponseEntity.ok(
                bookingService.getUserBookings(userId, status)
        );
    }

    @PutMapping("/bookings/{bookingId}/cancelBooking")
    public CancellationResponseDTO cancelBooking(
            @PathVariable Long bookingId,
            @RequestParam CancelledBy cancelledBy,
            @RequestParam(required = true) String reason){

        return bookingService.cancelBooking(bookingId, cancelledBy, reason);
    }

    @GetMapping("/{bookingId}/summary")
    public BookingSummaryDTO getBookingSummary(@PathVariable Long bookingId){
        return bookingService.getBookingSummary(bookingId);
    }
}
