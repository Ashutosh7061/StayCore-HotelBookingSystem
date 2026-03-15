package com.ashutosh.HotelBookingSystem.dto;

import com.ashutosh.HotelBookingSystem.Enum.RoomStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddRoomResponseDTO {

    private Long roomId;
    private String roomNumber;
    private String roomType;
    private double price;
    private RoomStatus status;

}
