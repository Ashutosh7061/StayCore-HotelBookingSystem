package com.ashutosh.HotelBookingSystem.dto;

import com.ashutosh.HotelBookingSystem.Enum.BookingSource;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class HotelBookingSummaryDTO {

    private Long bookingId;
    private String bookingReferenceId;
    private BookingSource bookingSource;

    private String userName;
    private String userPhoneNO;

    LocalDate checkInDate;
    LocalDate checkOutDate;

    private String roomType;
    private int numberOfRooms;

    private String status;

    private LocalDateTime bookingTime;

}

