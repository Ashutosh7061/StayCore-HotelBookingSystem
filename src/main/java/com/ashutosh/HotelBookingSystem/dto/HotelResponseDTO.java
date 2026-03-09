package com.ashutosh.HotelBookingSystem.dto;

import com.ashutosh.HotelBookingSystem.Enum.HotelStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class HotelResponseDTO {

    private Long hotelId;
    private String hotelName;
    private AddressDTO address;
    private String phoneNo;

    private HotelStatus status;

}
