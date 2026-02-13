package com.ashutosh.HotelBookingSystem.exception;

public class InvalidIdNumberException extends RuntimeException{
    public InvalidIdNumberException(String message){
        super(message);
    }
}
