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

    @ExceptionHandler(InvalidBookingStateException.class)
    public ResponseEntity<String> handleInvalidState(InvalidBookingStateException ex){
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
    }
    @ExceptionHandler(BookingValidationException.class)
    public ResponseEntity<String> handleBookingValidation(BookingValidationException ex){
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(BookingCheckoutException.class)
    public ResponseEntity<String> handleBookingCheckout(BookingCheckoutException ex){
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidRatingException.class)
    public ResponseEntity<String> handleInvalidRating(InvalidRatingException ex){
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BookingCheckInException.class)
    public ResponseEntity<String> handleEarlierCheckInDate(BookingCheckInException ex){
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
    }
    @ExceptionHandler(InvalidPhoneNumberException.class)
    public ResponseEntity<String> handleInvalidPhoneNumber(InvalidPhoneNumberException ex){
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UnauthorizedAccessException.class)
    public ResponseEntity<String> handleUnauthorizedAccess(UnauthorizedAccessException ex){
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(MissingValueException.class)
    public ResponseEntity<String > handleMissingValueException(MissingValueException ex){
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(HotelRegisterException.class)
    public ResponseEntity<String> handleHotelRegisterIssue(HotelRegisterException ex){
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(SupportRequestException.class)
    public ResponseEntity<String> handleSupportRequestIssue(SupportRequestException ex){
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RoomNotModifiableException.class)
    public ResponseEntity<String> handleRoomNotModified(RoomNotModifiableException ex){
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

}
