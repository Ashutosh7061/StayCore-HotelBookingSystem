package com.ashutosh.HotelBookingSystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlatformDashboardDTO {

    private  String str;

    private PlatformHotelCountStatsDTO hotelStats;
    private PlatformBookingStats bookingStats;
    private PlatformRevenueStats revenueStats;
}
