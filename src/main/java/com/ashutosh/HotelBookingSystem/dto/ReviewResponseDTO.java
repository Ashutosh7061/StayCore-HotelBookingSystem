package com.ashutosh.HotelBookingSystem.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class ReviewResponseDTO {

    private Long reviewId;
    private Long bookingId;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;

}
