package com.ashutosh.HotelBookingSystem.dto;

import com.ashutosh.HotelBookingSystem.Enum.IdType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileResponseDTO {

    private Long userId;
    private String name;
    private String email;
    private String phoneNo;
    private String address;
    private IdType uniqueIdType;
    private String uniqueIdNumber;

}
