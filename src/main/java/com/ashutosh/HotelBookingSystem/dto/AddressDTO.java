package com.ashutosh.HotelBookingSystem.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AddressDTO {

    private String addressLine;
    private String city;
    private String state;
    private String pinCode;
}
