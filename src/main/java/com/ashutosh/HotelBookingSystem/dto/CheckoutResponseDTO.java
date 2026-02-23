package com.ashutosh.HotelBookingSystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CheckoutResponseDTO {
    private Long bookingId;
    private String status;
    private String roomCondition;
    private Integer rating;
    private String review;
    private LocalDateTime checkoutTime;
}
