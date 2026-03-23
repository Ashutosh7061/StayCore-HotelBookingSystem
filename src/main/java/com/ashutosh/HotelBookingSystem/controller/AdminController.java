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
import org.springframework.security.core.parameters.P;
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

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users")
    public List<AdminUserDetailsDTO> getAllUsers(){
        return adminService.getAllUsersForAdmin();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/hotels")
    public List<AdminHotelListDTO> getAllHotels(){
        return hotelService.getHotelListForAdmin();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/hotel/{hotelId}")
    public ResponseEntity<AdminHotelDashboardDTO> getSpecificHotelDetails(@PathVariable Long hotelId){
        return ResponseEntity.ok(adminService.getSpecificHotelDetails(hotelId));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/hotel/{hotelId}/users")
    public ResponseEntity<List<BaseUserPerHotelResponseDTO>> getAllUserOfHotelByStatus(
            @PathVariable Long hotelId, @RequestParam(required = false) BookingStatus status){
        return ResponseEntity.ok(bookingService.getAllUserOfHotelByStatus(hotelId,status));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/completed-bookings")
    public List<AdminBookingDetailsDTO> getCompletedBookings(){
        return bookingService.getCompletedBookingForAdmin();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/dashboard")
    public PlatformDashboardDTO getDashBoard(){
        return adminService.getPlatformDashboard();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/hotel/{hotelId}/allRooms")
    public ApiResponseDTO getRooms(@PathVariable Long hotelId){

        List<AdminRoomDTO> rooms = roomService.getRoomsByHotel(hotelId);

        int roomCount = rooms.size();

        return new ApiResponseDTO("Total rooms: "+roomCount ,rooms);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/hotels/pending")
    public AdminPendingHotelResponseDTO getPendingHotels(){
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
    @PutMapping("/support/{tokenId}/resolve")
    public String resolveRequest(@PathVariable String tokenId){
        return adminService.resolveSupportRequest(tokenId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/support/{tokenId}/reply")
    public String replyToSupportRequest(@PathVariable String tokenId, @RequestBody SupportReplyDTO request){
        return adminService.replyToSupportRequest(tokenId, request.getMessage());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/support/token/{tokenId}")
    public SupportTicketResponseDTO getTicketByToken(@PathVariable String tokenId){
        return adminService.getTicketByTokenAdmin(tokenId);
    }
}
