package com.ashutosh.HotelBookingSystem.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AddressDTO {

    private String addressLine;
    private String city;
    private String state;
    private String pinCode;
}
