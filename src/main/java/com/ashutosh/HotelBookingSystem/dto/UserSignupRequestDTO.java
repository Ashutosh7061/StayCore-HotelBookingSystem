package com.ashutosh.HotelBookingSystem.dto;


import com.ashutosh.HotelBookingSystem.Enum.IdType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserSignupRequestDTO {

    private String name;
    private String email;
    private String phoneNo;
    private String address;
    private IdType uniqueIdType;
    private String uniqueIdNumber;
    private String password;
}
