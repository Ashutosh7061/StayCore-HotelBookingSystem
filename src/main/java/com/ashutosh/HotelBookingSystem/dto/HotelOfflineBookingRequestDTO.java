package com.ashutosh.HotelBookingSystem.dto;

import com.ashutosh.HotelBookingSystem.Enum.IdType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Data
@Setter
@Getter
public class HotelOfflineBookingRequestDTO {
    private String roomType;
    private int noOfRooms;

    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate checkInDate;
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate checkOutDate;

    private String guestName;
    private String guestPhoneNo;
    private String guestEmail;
    private String guestAddress;
    private IdType guestIdType;
    private String guestIdNumber;

    private Boolean duplicateBooking;
}
