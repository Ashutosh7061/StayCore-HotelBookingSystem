package com.ashutosh.HotelBookingSystem.dto;

import com.ashutosh.HotelBookingSystem.Enum.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminBookingDetailsDTO {

    private Long bookingId;
    private Long userId;
    private String userName;
    private Long hotelId;
    private String hotelName;

    private double totalPrice;
    private BookingStatus status;

    private Integer rating;
    private  String review;
}
