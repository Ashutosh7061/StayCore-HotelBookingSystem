package com.ashutosh.HotelBookingSystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HotelDashboardBookingStatsDTO {
    private Long totalBookings;
    private Long confirmedBookings;
    private Long completedBookings;
    private Long cancelledBookings;
}
