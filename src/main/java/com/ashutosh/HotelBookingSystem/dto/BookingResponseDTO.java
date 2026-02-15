package com.ashutosh.HotelBookingSystem.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookingResponseDTO {

    private Long bookingId;

    private Long userId;
    private String userName;

    private Long hotelId;
    private String hotelName;

    private List<String> allottedRoomNumber;

    private int numberOfDays;
    private int numberOfRooms;

    private LocalDate checkInDate;
    private LocalDate checkOutDate;

    private double totalPrice;
    private String status;

    private LocalDateTime bookingTime;
}
