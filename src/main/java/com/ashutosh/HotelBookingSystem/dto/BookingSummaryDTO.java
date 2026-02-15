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
public class BookingSummaryDTO {

    private Long bookingId;

    private Long userId;
    private String userName;
    private String userPhone;

    private Long hotelId;
    private String hotelName;
    private String hotelAddress;

    private List<String> allottedRoomNumbers;

    private int numberOfDays;
    private int numberOfRooms;

    private double totalPrice;

    private String status;

    private LocalDateTime bookingTime;


}
