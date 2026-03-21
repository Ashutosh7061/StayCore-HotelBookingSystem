package com.ashutosh.HotelBookingSystem.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewRequestDTO {
    private Long bookingId;
    private Integer rating;
    private String comment;
}
