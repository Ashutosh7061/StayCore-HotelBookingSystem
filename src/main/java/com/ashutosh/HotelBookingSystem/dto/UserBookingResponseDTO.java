package com.ashutosh.HotelBookingSystem.dto;

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
    private String hotelAddress;
    private List<String> allottedRoomNumber;
    private int numberOfRooms;
    private int numberOfDays;

    private double totalPrice;

    private String status;

    private LocalDateTime bookingTime;



}
