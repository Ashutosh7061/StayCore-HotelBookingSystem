package com.ashutosh.HotelBookingSystem.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserBookingRequestDTO {
    private Long hotelId;
    private String roomType;
    private int noOfRooms;

    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate checkInDate;

    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate checkOutDate;

    private String checkInInstruction;

    private  Boolean duplicateBooking;
}
