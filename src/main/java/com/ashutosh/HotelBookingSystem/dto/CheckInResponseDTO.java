package com.ashutosh.HotelBookingSystem.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
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

    @JsonFormat(pattern = "dd-MM-yyyy HH-mm-ss")
    private LocalDateTime checkInTime;

    private String requestMessage;
}
