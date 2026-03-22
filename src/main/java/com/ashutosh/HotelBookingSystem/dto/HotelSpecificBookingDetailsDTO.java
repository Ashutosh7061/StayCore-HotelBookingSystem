package com.ashutosh.HotelBookingSystem.dto;


import com.ashutosh.HotelBookingSystem.Enum.BookingSource;
import com.ashutosh.HotelBookingSystem.Enum.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HotelSpecificBookingDetailsDTO {

    private Long bookingId;
    private String userName;
    private String userPhoneNo;
    private String userEmail;

    private BookingSource bookingSource;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;

    private String roomType;
    private int noOfRooms;
    private int noOfDays;

    private List<String> allottedRoomNumber;
    private Double totalPrice;
    private BookingStatus status;

    private LocalDateTime bookingTime;
}
