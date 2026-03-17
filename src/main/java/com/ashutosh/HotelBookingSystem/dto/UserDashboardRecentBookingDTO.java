package com.ashutosh.HotelBookingSystem.dto;

import com.ashutosh.HotelBookingSystem.Enum.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDashboardRecentBookingDTO {

    private String bookingReferenceId;
    private String hotelName;
    private String roomType;

    private LocalDate checkInDate;
    private LocalDate checkOutDate;

    private BookingStatus status;

    private Double totalPrice;
}
