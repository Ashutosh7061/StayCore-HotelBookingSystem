package com.ashutosh.HotelBookingSystem.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminFinancialStatsDTO {

    private Double totalBookingRevenue;
    private Double totalCommission;
    private Double totalBookingCommission;
    private Double totalRegistrationCommission;
}
