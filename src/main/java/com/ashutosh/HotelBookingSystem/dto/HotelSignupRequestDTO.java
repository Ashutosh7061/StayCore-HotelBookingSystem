package com.ashutosh.HotelBookingSystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HotelSignupRequestDTO {

    private String hotelName;
    private String addressLine;
    private String city;
    private String state;
    private String pinCode;
    private String phoneNo;
    private String email;
    private String registeredOwnerName;
    private String govtRegisteredNo;
    private String gstNo;
    private String password;

}
