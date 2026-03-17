package com.ashutosh.HotelBookingSystem.exception;

public class RoomNotModifiableException extends RuntimeException {
    public RoomNotModifiableException(String message){
        super(message);
    }
}
