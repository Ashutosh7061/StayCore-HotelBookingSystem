package com.ashutosh.HotelBookingSystem.exception;

public class InvalidCheckOutDateException extends RuntimeException{
    public InvalidCheckOutDateException(String message){
        super(message);
    }
}
