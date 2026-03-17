package com.ashutosh.HotelBookingSystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDashboardDTO {
    private UserDashboardInfoDTO userInfo;
    private UserDashboardBookingStatsDTO bookingInfo;
    private List<UserDashboardRecentBookingDTO> recentBookings;
}
