package com.ashutosh.HotelBookingSystem.exception;

public class InvalidRatingException extends RuntimeException{
    public InvalidRatingException(String message){
        super(message);
    }
}
