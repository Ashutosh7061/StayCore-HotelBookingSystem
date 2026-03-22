package com.ashutosh.HotelBookingSystem.controller;

import com.ashutosh.HotelBookingSystem.Enum.BookingStatus;
import com.ashutosh.HotelBookingSystem.Enum.CancelledBy;
import com.ashutosh.HotelBookingSystem.dto.*;
import com.ashutosh.HotelBookingSystem.entity.Hotel;
import com.ashutosh.HotelBookingSystem.service.BookingService;
import com.ashutosh.HotelBookingSystem.service.HotelService;
import com.ashutosh.HotelBookingSystem.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final BookingService bookingService;
    private final HotelService hotelService;
    private final UserService userService;


    @GetMapping("/allAvailableHotels")
    public List<Hotel> getAllAvailableHotels(){
        return hotelService.getAllAvailableHotels();
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/make-booking")
    public ResponseEntity<ApiResponseDTO> bookRoom(@RequestBody UserOnlineBookingRequestDTO request){
        BookingResponseDTO  addedRoom = bookingService.bookRoom(request);
        ApiResponseDTO response = new ApiResponseDTO("Your booking is successfully completed", addedRoom);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/my-bookings")
    public ResponseEntity<List<UserBookingResponseDTO>> getIndividualUserBookings(
            @RequestParam(required = false) BookingStatus status){

        return ResponseEntity.ok(bookingService.getIndividualUserBookings(status)
        );
    }

    @PreAuthorize("hasRole('USER')")
    @PutMapping("/bookings/{bookingId}/cancelBooking")
    public ResponseEntity<ApiResponseDTO> cancelBooking(
            @PathVariable Long bookingId,
            @RequestParam CancelledBy cancelledBy,
            @RequestParam(required = true) String reason){

        CancellationResponseDTO cancelled = bookingService.cancelBooking(bookingId, cancelledBy, reason);

        ApiResponseDTO response = new ApiResponseDTO("Your booking is successfully cancelled", cancelled);

        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/{bookingId}/summary")
    public ResponseEntity<BookingSummaryDTO> getBookingSummary(@PathVariable Long bookingId){
        return ResponseEntity.ok(bookingService.getBookingSummary(bookingId));
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponseDTO> getUserProfile(){
        return ResponseEntity.ok(userService.getUserProfile());
    }

    @PreAuthorize("hasRole('USER')")
    @PutMapping("/profile")
    public ResponseEntity<ApiResponseDTO> updateUserProfile(@RequestBody UserProfileUpdateDTO request){

        UserProfileResponseDTO updatedUser = userService.updateUserProfile(request);

        ApiResponseDTO response = new ApiResponseDTO("Profile successfully updated", updatedUser);

        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/dashboard")
    public ResponseEntity<UserDashboardDTO> getUserDashboard(){
        return ResponseEntity.ok(userService.getUserDashboard());
    }


    @PreAuthorize("hasRole('USER')")
    @PostMapping("/review")
    public ResponseEntity<ApiResponseDTO> addReview(@RequestBody ReviewRequestDTO request){

        ReviewResponseDTO review = userService.addReview(request);

        ApiResponseDTO response = new ApiResponseDTO("Review submitted successfully", review);

        return ResponseEntity.ok(response);
    }
}
