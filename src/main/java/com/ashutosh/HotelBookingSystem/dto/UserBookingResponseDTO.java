package com.ashutosh.HotelBookingSystem.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserBookingResponseDTO {

    private Long bookingId;
    private String hotelName;
    private AddressDTO address;
    private int numberOfRooms;
    private int numberOfDays;

    private String status;

    @JsonFormat(pattern = "dd-MM-yyyy HH-mm-ss" )
    private LocalDateTime bookingTime;



}
