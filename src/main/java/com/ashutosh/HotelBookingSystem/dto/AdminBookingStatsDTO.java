package com.ashutosh.HotelBookingSystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminBookingStatsDTO {
    private Long totalRooms;
    private Long totalBooking;
    private Long confirmedBooking;
    private Long completedBooking;
    private Long cancelledBooking;

    private Long onlineBookings;
    private Long offlineBookings;
}
