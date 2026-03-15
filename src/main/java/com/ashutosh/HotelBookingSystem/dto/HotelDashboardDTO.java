package com.ashutosh.HotelBookingSystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HotelDashboardDTO {
    private HotelDashboardInfoDTO hotelInfo;
    private HotelDashboardRoomStatsDTO roomInfo;
    private HotelDashboardBookingStatsDTO bookingInfo;
    private HotelDashboardFinancialStatsDTO financialInfo;
}
