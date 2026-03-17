package com.ashutosh.HotelBookingSystem.Mapper;

import com.ashutosh.HotelBookingSystem.Enum.IdType;
import com.ashutosh.HotelBookingSystem.entity.Booking;
import com.ashutosh.HotelBookingSystem.exception.DataNotFoundException;
import com.ashutosh.HotelBookingSystem.exception.InvalidIdNumberException;
import com.ashutosh.HotelBookingSystem.exception.InvalidPhoneNumberException;
import com.ashutosh.HotelBookingSystem.repository.BookingRepository;
import lombok.RequiredArgsConstructor;

import java.time.temporal.ChronoUnit;

@RequiredArgsConstructor
public class helperFunctions {

    private final BookingRepository bookingRepository;

    public static int calculateDays(Booking booking){
        return (int) ChronoUnit.DAYS.between(
                booking.getCheckInDate(),
                booking.getCheckOutDate()
        );
    }

    public static void validateGovernmentId(IdType idType, String uniqueIdNumber) {

        if (idType == IdType.AADHAR) {
            if (!uniqueIdNumber.matches("\\d{12}")) {
                throw new InvalidIdNumberException(
                        "Aadhaar must be exactly 12 digits"
                );
            }
        }
        if (idType == IdType.VOTER_CARD) {
            if (!uniqueIdNumber.matches("^[A-Z]{3}[0-9]{7}$")) {
                throw new InvalidIdNumberException(
                        "Voter ID must be 3 uppercase letters followed by 7 digits"
                );
            }
        }
    }

    public static void isValidPhoneNumber(String phoneNumber) {
        if (phoneNumber == null) {
            throw new DataNotFoundException("Phone number can not be null.");
        }

        phoneNumber = phoneNumber.trim();

        if (phoneNumber.length() != 10) {
            throw new InvalidPhoneNumberException("Phone number must be exactly 10 digits.");
        }
        char firstDigit = phoneNumber.charAt(0);
        if (firstDigit < '6' || firstDigit > '9') {
            throw new InvalidPhoneNumberException("Phone number must start with digits  6-9");
        }
        for (int i = 0; i < phoneNumber.length(); i++) {
            if (!Character.isDigit(phoneNumber.charAt(i))) {
                throw new InvalidPhoneNumberException("Phone number must contains only digits.");
            }
        }
    }

}
