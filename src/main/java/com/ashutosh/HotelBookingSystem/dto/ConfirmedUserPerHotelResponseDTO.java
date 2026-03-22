package com.ashutosh.HotelBookingSystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConfirmedUserPerHotelResponseDTO extends BaseUserPerHotelResponseDTO{

    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private int numberOfRooms;

    public ConfirmedUserPerHotelResponseDTO(
            Long userId,
            String customerType,
            String name,
            String email,
            String phoneNo,
            LocalDateTime createdAt,
            LocalDate checkInDate,
            LocalDate checkOutDate,
            int numberOfRooms
    ){
        super(userId,customerType, name, email, phoneNo, createdAt);
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.numberOfRooms = numberOfRooms;
    }
}
