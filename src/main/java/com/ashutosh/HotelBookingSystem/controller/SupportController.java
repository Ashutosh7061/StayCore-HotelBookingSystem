package com.ashutosh.HotelBookingSystem.controller;


import com.ashutosh.HotelBookingSystem.dto.SupportRequestDTO;
import com.ashutosh.HotelBookingSystem.service.SupportService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/support")
public class SupportController {

    private final SupportService supportService;

    @PostMapping("/submit-query")
    public String createSupportRequest(@RequestBody SupportRequestDTO message){
        return supportService.createSupportRequest(message);
    }
}
