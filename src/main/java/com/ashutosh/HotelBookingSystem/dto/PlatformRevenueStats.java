package com.ashutosh.HotelBookingSystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PlatformRevenueStats {
    private Double registrationCommission;
    private Double bookingCommission;
    private Double totalPlatformEarning;
}
