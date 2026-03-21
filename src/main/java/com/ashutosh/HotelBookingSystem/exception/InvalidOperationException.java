package com.ashutosh.HotelBookingSystem.exception;

public class InvalidOperationException extends RuntimeException{
    public InvalidOperationException(String message){
        super(message);
    }
}
