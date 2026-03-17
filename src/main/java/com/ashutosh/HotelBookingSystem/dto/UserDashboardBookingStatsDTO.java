package com.ashutosh.HotelBookingSystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDashboardBookingStatsDTO {
    private Long totalBooking;
    private Double totalSpend;
    private Long confirmedBookings;
    private Long completedBookings;
    private Long CancelledBokings;
}
