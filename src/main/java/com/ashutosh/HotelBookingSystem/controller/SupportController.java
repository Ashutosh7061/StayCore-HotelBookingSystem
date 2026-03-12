package com.ashutosh.HotelBookingSystem.controller;


import com.ashutosh.HotelBookingSystem.dto.SupportReplyDTO;
import com.ashutosh.HotelBookingSystem.dto.SupportRequestDTO;
import com.ashutosh.HotelBookingSystem.dto.SupportTicketResponseDTO;
import com.ashutosh.HotelBookingSystem.service.SupportService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/support")
public class SupportController {

    private final SupportService supportService;

    @PostMapping("/submit-query")
    public String createSupportRequest(@RequestBody SupportRequestDTO message){
        return supportService.createSupportRequest(message);
    }

    @GetMapping("/ticket/{tokenId}")
    public SupportTicketResponseDTO getTicketStatus(@PathVariable String tokenId){
        return supportService.getTicketByToken(tokenId);
    }

    @PostMapping("/ticket/{tokenId}/reply")
    public String replyToTicket(@PathVariable String tokenId, @RequestBody SupportReplyDTO request){
        return supportService.replyToTicketByUserOrHotel(tokenId, request.getMessage());
    }
}
