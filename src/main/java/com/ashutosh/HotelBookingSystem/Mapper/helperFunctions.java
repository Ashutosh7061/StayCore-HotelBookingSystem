package com.ashutosh.HotelBookingSystem.Mapper;

import com.ashutosh.HotelBookingSystem.entity.Booking;

import java.time.temporal.ChronoUnit;

public class helperFunctions {

    public static int calculateDays(Booking booking){
        return (int) ChronoUnit.DAYS.between(
                booking.getCheckInDate(),
                booking.getCheckOutDate()
        );
}
}
