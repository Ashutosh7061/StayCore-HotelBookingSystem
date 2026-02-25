package com.ashutosh.HotelBookingSystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminRoomDTO {

    private Long roomId;
    private String roomNumber;
    private String roomType;
    private double price;
    private String currentStatus;
}
