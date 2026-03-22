package com.ashutosh.HotelBookingSystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
@AllArgsConstructor
public class PlatformHotelCountStatsDTO {

    private Long totalHotels;
    private Long approvedHotels;
    private Long pendingHotels;
    private Long blockedHotels;
    private Long rejectedHotels;

}
