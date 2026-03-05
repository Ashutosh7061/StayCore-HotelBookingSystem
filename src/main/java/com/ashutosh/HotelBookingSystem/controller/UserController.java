package com.ashutosh.HotelBookingSystem.controller;

import com.ashutosh.HotelBookingSystem.Enum.BookingStatus;
import com.ashutosh.HotelBookingSystem.Enum.CancelledBy;
import com.ashutosh.HotelBookingSystem.dto.*;
import com.ashutosh.HotelBookingSystem.entity.User;
import com.ashutosh.HotelBookingSystem.service.BookingService;
import com.ashutosh.HotelBookingSystem.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final BookingService bookingService;


    @PostMapping
    public BookingResponseDTO bookRoom(@RequestBody UserBookingRequestDTO request){
        return bookingService.bookRoom(request);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/my-bookings")
    public ResponseEntity<List<UserBookingResponseDTO>> getIndividualUserBookings(
            @RequestParam(required = false) BookingStatus status){

        return ResponseEntity.ok(bookingService.getIndividualUserBookings(status)
        );
    }

    @PutMapping("/bookings/{bookingId}/cancelBooking")
    public CancellationResponseDTO cancelBooking(
            @PathVariable Long bookingId,
            @RequestParam CancelledBy cancelledBy,
            @RequestParam(required = true) String reason){

        return bookingService.cancelBooking(bookingId, cancelledBy, reason);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/{bookingId}/summary")
    public ResponseEntity<BookingSummaryDTO> getBookingSummary(@PathVariable Long bookingId){
        return ResponseEntity.ok(bookingService.getBookingSummary(bookingId));
    }
}
