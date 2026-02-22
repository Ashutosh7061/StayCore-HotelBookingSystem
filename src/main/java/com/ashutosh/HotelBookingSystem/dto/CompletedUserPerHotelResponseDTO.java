package com.ashutosh.HotelBookingSystem.dto;

import java.time.LocalDateTime;

public class CompletedUserPerHotelResponseDTO extends BaseUserPerHotelResponseDTO{

    private String review;
    private Integer rating;

    public CompletedUserPerHotelResponseDTO(
            Long userId,
            String name,
            String email,
            String phoneNo,
            LocalDateTime createdAt,
            String review,
            Integer rating) {

        super(userId, name, email, phoneNo, createdAt);
        this.review = review;
        this.rating = rating;
    }

}
