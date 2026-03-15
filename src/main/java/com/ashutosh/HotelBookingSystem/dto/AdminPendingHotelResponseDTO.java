package com.ashutosh.HotelBookingSystem.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminPendingHotelResponseDTO {

    private Long totalPending;
    private List<AdminHotelApprovingDTO> hotels;

}
