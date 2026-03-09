package com.ashutosh.HotelBookingSystem.dto;

import com.ashutosh.HotelBookingSystem.Enum.HotelStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminHotelDetailsDTO {

    private Long hotelId;
    private String hotelName;
    private String phoneNo;
    private String email;
    private AddressDTO address;

    private HotelStatus status;

    private Long totalBooking;
    private Long completedBooking;
    private Long cancelledBooking;
    private Long confirmedBooking;

    private Double totalCommission;
    private Double totalBookingCommission;
    private Double totalRegistrationCommission;
}
