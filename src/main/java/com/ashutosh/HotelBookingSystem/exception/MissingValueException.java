package com.ashutosh.HotelBookingSystem.exception;

public class MissingValueException extends RuntimeException{
    public MissingValueException(String message){
        super(message);
    }
}
