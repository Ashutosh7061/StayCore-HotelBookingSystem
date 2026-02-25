package com.ashutosh.HotelBookingSystem.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class HotelResponseDTO {

    private Long hotelId;
    private String hotelName;
    private AddressDTO address;
    private String contact;

}
