package com.ashutosh.HotelBookingSystem.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DuplicateDataException.class)
    public ResponseEntity<String> handleDuplicateUniqueId(DuplicateDataException ex){
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
    }


    @ExceptionHandler(InvalidIdNumberException.class)
    public ResponseEntity<String> handleInvalidIdNumber(InvalidIdNumberException ex){
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<String> handleDuplicateException(
            DataIntegrityViolationException ex) {

        String message = ex.getRootCause().getMessage();

        if (message.contains("UK_HOTEL_CONTACT")) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body("Phone number already exists");
        }

        if (message.contains("UK_HOTEL_LOCATION")) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body("Hotel already exists at this location");
        }

        return ResponseEntity
                .status(HttpStatus.CONFLICT).body("Duplicate data found");
    }

    @ExceptionHandler(DataNotFoundException.class)
    public ResponseEntity<String> handleDataNotFoundException(DataNotFoundException ex){
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidCheckOutDateException.class)
    public ResponseEntity<String> handleInvalidCheckOutDate(InvalidCheckOutDateException ex){
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }


}
