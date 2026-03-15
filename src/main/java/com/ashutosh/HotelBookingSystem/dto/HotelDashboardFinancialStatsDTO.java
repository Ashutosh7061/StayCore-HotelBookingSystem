package com.ashutosh.HotelBookingSystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HotelDashboardFinancialStatsDTO {
    private Double totalBookingRevenue;
    private Double totalCommission;
    private Double netEarning;
}
