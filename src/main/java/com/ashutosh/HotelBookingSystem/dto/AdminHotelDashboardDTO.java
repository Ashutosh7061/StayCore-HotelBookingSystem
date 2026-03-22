package com.ashutosh.HotelBookingSystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminHotelDashboardDTO {

    private AdminHotelFullInfoDTO hotelInfo;
    private AdminBookingStatsDTO bookingStats;
    private AdminFinancialStatsDTO financialStats;

}
