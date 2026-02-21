package com.ashutosh.HotelBookingSystem.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class HotelResponseDTO {

    private Long hotelId;
    private String hotelName;
    private String addressLine;
    private String city;
    private String state;
    private String pinCode;
    private String contact;

}
