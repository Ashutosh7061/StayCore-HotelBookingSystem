package com.ashutosh.HotelBookingSystem.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlatformDashboardDTO {

    private  String str;

    private Long totalCompletedBookings;

    private Double totalRegistrationCommission;

    private Double totalBookingCommission;

    private Double totalPlatformEarning;
}
