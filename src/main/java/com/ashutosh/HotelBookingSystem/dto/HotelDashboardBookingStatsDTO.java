package com.ashutosh.HotelBookingSystem.dto;

import com.ashutosh.HotelBookingSystem.Enum.BookingSource;
import lombok.*;

@Data
@AllArgsConstructor
@Getter
@Setter
public class HotelDashboardBookingStatsDTO {
    private Long totalBookings;
    private Long confirmedBookings;
    private Long completedBookings;
    private Long cancelledBookings;

    private Long onlineBooking;
    private Long offlineBooking;
}
