package com.ashutosh.HotelBookingSystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlatformDashboardDTO {

    private Long totalCompletedBookings;

    private Double totalRegistrationCommission;

    private Double totalBookingCommission;

    private Double totalPlatformEarning;
}
