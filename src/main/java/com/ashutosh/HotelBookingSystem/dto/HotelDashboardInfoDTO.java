package com.ashutosh.HotelBookingSystem.dto;

import com.ashutosh.HotelBookingSystem.Enum.HotelStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HotelDashboardInfoDTO {
    private Long hotelId;
    private String hotelName;
    private HotelStatus status;

    @JsonFormat(pattern = "dd-MM-yyyy HH-mm-ss")
    private LocalDateTime createdAt;
}
