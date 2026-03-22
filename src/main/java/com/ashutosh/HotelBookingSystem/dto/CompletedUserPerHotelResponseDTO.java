package com.ashutosh.HotelBookingSystem.dto;

import java.time.LocalDateTime;

public class CompletedUserPerHotelResponseDTO extends BaseUserPerHotelResponseDTO{

    private String review;
    private Integer rating;

    public CompletedUserPerHotelResponseDTO(
            Long userId,
            String customerType,
            String name,
            String email,
            String phoneNo,
            LocalDateTime createdAt,
            String review,
            Integer rating) {

        super(userId,customerType, name, email, phoneNo, createdAt);
        this.review = review;
        this.rating = rating;
    }

}
