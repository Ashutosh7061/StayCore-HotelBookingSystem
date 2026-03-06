package com.ashutosh.HotelBookingSystem.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.security.Principal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponseDTO {
    Long userId;
    private String role;
    String jwt;
}
