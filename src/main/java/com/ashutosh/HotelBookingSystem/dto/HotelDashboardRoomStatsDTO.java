package com.ashutosh.HotelBookingSystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HotelDashboardRoomStatsDTO {
    private Long totalRooms;

    private Long singleRoomCount;
    private Long doubleRoomCount;
    private Long deluxeRoomCount;
}
