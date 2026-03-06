package com.ashutosh.HotelBookingSystem.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckOutRequestDTO {
    private Long bookingId;
    private Long userId;
    private String uniqueIdNumber;
    private String review;
    private Integer rating;
    private String roomCondition;
}
