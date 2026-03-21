package com.ashutosh.HotelBookingSystem.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckOutRequestDTO {
    private Long bookingId;
    private String bookingReferenceId;

    private String roomCondition;
}
