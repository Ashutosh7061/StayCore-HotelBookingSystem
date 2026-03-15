package com.ashutosh.HotelBookingSystem.dto;

import com.ashutosh.HotelBookingSystem.Enum.RoomStatus;
import com.ashutosh.HotelBookingSystem.entity.Hotel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddRoomRequestDTO {

    private String roomNumber;
    private String roomType;
    private double price;
    private RoomStatus status;
    private Hotel hotel;
}
