package com.ashutosh.HotelBookingSystem.exception;

public class DuplicateDataException extends RuntimeException{
    public DuplicateDataException(String message){
        super(message);
    }
}
