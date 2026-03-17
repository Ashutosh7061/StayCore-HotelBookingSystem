package com.ashutosh.HotelBookingSystem.controller;


import com.ashutosh.HotelBookingSystem.Enum.CancelledBy;
import com.ashutosh.HotelBookingSystem.dto.*;
import com.ashutosh.HotelBookingSystem.repository.RoomRepository;
import com.ashutosh.HotelBookingSystem.service.BookingService;
import com.ashutosh.HotelBookingSystem.service.HotelService;
import com.ashutosh.HotelBookingSystem.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/hotel")
@RequiredArgsConstructor
public class HotelController {

    private final HotelService hotelService;
    private final BookingService bookingService;
    private final RoomService roomService;
    private final RoomRepository roomRepository;


    @PreAuthorize("hasRole('HOTEL')")
    @PostMapping("/addRoom")
    public ResponseEntity<ApiResponseDTO> addRoom(@RequestBody AddRoomRequestDTO room){

        RoomResponseDTO addedRoom = roomService.addRoom(room);
        ApiResponseDTO response = new ApiResponseDTO("Room added successfully", addedRoom);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PreAuthorize("hasRole('HOTEL')")
    @PutMapping("/bookings/{bookingId}/cancelBooking")
    public ResponseEntity<ApiResponseDTO> cancelBooking(
            @PathVariable Long bookingId,
            @RequestParam CancelledBy cancelledBy,
            @RequestParam(required = true) String reason){

        CancellationResponseDTO cancelled = bookingService.cancelBooking(bookingId, cancelledBy, reason);

        ApiResponseDTO response = new ApiResponseDTO("Booking successfully cancelled", cancelled);

        return ResponseEntity.ok(response);
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

    @PreAuthorize("hasRole('HOTEL')")
    @GetMapping("/dashboard")
    public HotelDashboardDTO getHotelDashboard(){
        return hotelService.getHotelDashboard();
    }

    @PreAuthorize("hasRole('HOTEL')")
    @GetMapping("/profile")
    public HotelProfileResponseDTO getHotelProfile(){
        return hotelService.getHotelProfile();
    }

    @PreAuthorize("hasRole('HOTEL')")
    @PutMapping("/profile")
    public ApiResponseDTO getHotelProfile(@RequestBody HotelProfileUpdateDTO request){

        HotelProfileResponseDTO updatedDetails = hotelService.updateHotelProfile(request);

        ApiResponseDTO response = new ApiResponseDTO("Profile updated successfully", updatedDetails);

        return response;
    }

    @PreAuthorize("hasRole('HOTEL')")
    @PutMapping("/update/{roomNo}")
    public ApiResponseDTO updateRoomDetails(@PathVariable String roomNo,@RequestBody RoomUpdateDTO request){

        RoomResponseDTO updatedRoom = roomService.updateRoom(roomNo, request);
        ApiResponseDTO response = new ApiResponseDTO("Room Updated Successfully", updatedRoom);

        return response;
    }

    @PreAuthorize("hasRole('HOTEL')")
    @DeleteMapping("/delete/{roomNo}")
    public String deleteRoom(@PathVariable String roomNo){
        return roomService.deleteRoom(roomNo);
    }
}
