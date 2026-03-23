package com.ashutosh.HotelBookingSystem.dto;

import com.ashutosh.HotelBookingSystem.Enum.HotelStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class AdminHotelListDTO {

    private Long hotelId;
    private String hotelName;
    private AddressDTO address;

    @JsonFormat(pattern = "dd-MM-yyyy HH-mm-ss")
    private LocalDateTime createdAt;
    private HotelStatus status;

}
