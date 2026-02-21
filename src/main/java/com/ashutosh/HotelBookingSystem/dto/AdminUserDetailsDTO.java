package com.ashutosh.HotelBookingSystem.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AdminUserDetailsDTO {

    private Long userId;
    private String name;
    private String email;
    private String phoneNo;

    private LocalDateTime registrationDate;

    private Double totalSpend;
    private Long totalBookings;
}
