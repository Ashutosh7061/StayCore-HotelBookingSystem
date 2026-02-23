package com.ashutosh.HotelBookingSystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CheckInResponseDTO {
    private Long bookingId;
    private String userName;
    private List<String> roomNumber;
    private LocalDateTime checkInTime;
}
