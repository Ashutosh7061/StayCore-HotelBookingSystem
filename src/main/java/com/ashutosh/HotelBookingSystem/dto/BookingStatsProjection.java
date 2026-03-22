package com.ashutosh.HotelBookingSystem.dto;

public interface BookingStatsProjection {

    Long getTotalBookings();
    Long getConfirmedBookings();
    Long getCompletedBookings();
    Long getCancelledBookings();
    Long getOnlineBookings();
    Long getOfflineBookings();
}