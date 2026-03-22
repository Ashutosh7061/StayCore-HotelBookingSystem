package com.ashutosh.HotelBookingSystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
@AllArgsConstructor
public class PlatformBookingStats {
    private Long totalBooking;
    private Long confirmedBooking;
    private Long completedBooking;
    private Long cancelledBooking;

    private Long onlineBooking;
    private Long offlineBooking;
}
