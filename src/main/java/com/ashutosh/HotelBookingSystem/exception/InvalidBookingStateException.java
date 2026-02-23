package com.ashutosh.HotelBookingSystem.exception;

public class InvalidBookingStateException extends BookingException{
    public InvalidBookingStateException(String message){
        super(message);
    }
}
