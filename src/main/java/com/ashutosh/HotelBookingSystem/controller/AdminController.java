package com.ashutosh.HotelBookingSystem.controller;

import com.ashutosh.HotelBookingSystem.Enum.BookingStatus;
import com.ashutosh.HotelBookingSystem.dto.*;
import com.ashutosh.HotelBookingSystem.entity.Hotel;
import com.ashutosh.HotelBookingSystem.entity.SupportRequest;
import com.ashutosh.HotelBookingSystem.service.AdminService;
import com.ashutosh.HotelBookingSystem.service.BookingService;
import com.ashutosh.HotelBookingSystem.service.HotelService;
import com.ashutosh.HotelBookingSystem.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final HotelService hotelService;
    private final AdminService adminService;
    private final BookingService bookingService;
    private final RoomService roomService;

    @GetMapping("/users")
    public List<AdminUserDetailsDTO> getAllUsers(){
        return adminService.getAllUsersForAdmin();
    }

    @GetMapping("/hotels")
    public List<HotelResponseDTO> getAllHotels(){
        return hotelService.getAllHotels();
    }

    @GetMapping("/hotel/{hotelId}")
    public ResponseEntity<AdminHotelDetailsDTO> getSpecificHotel(@PathVariable Long hotelId){
        return ResponseEntity.ok(adminService.getSpecificHotelDetails(hotelId));
    }

    @GetMapping("/hotel/{hotelId}/users")
    public ResponseEntity<List<BaseUserPerHotelResponseDTO>> getAllUserOfHotelByStatus(
            @PathVariable Long hotelId, @RequestParam(required = false) BookingStatus status){
        return ResponseEntity.ok(bookingService.getAllUserOfHotelByStatus(hotelId,status));
    }

    @GetMapping("/completed-bookings")
    public List<AdminBookingDetailsDTO> getCompletedBookings(){
        return bookingService.getCompletedBookingForAdmin();
    }

    @GetMapping("/dashboard")
    public PlatformDashboardDTO getDashBoard(){
        return adminService.getPlatformDashboard();
    }

    @GetMapping("/hotel/{hotelId}/allRooms")
    public List<AdminRoomDTO> getRooms(@PathVariable Long hotelId){
        return roomService.getRoomsByHotel(hotelId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/hotels/pending")
    public List<Hotel> getPendingHotels(){
        return adminService.getPendingHotels();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/hotel/{hotelId}/approve")
    public String approveHotel(@PathVariable Long hotelId){
        return adminService.approveHotel(hotelId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/hotel/{hotelId}/reject")
    public String rejectHotel(@PathVariable Long hotelId, @RequestParam String reason){
        return adminService.rejectHotel(hotelId,reason);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/hotel/{hotelId}/block")
    public String blockHotel(@PathVariable Long hotelId){
        return adminService.blockHotel(hotelId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/hotel/{hotelId}/unblock")
    public String unblockHotel(@PathVariable Long hotelId){
        return adminService.unblockHotel(hotelId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/support/open")
    public List<SupportRequest> getOpenRequests(){
        return adminService.getOpenSupportRequests();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/support/{id}/resolve")
    public String resolveRequest(@PathVariable String tokenId){
        return adminService.resolveSupportRequest(tokenId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/support/{id}/reply")
    public String replyToSupportRequest(@PathVariable String tokenId, @RequestBody SupportReplyDTO request){
        return adminService.replyToSupportRequest(tokenId, request.getMessage());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/support/token/{tokenId}")
    public SupportTicketResponseDTO getTicketByToken(@PathVariable String tokenId){
        return adminService.getTicketByTokenAdmin(tokenId);
    }
}
