package com.ashutosh.HotelBookingSystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminHotelDetailsDTO {

    private Long hotelId;
    private String hotelName;
    private String address;
    private String contact;

    private Double totalCommission;
    private Double totalBookingCommission;
    private Double totalRegistrationCommission;
}
