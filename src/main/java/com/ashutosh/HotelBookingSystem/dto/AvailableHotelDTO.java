package com.ashutosh.HotelBookingSystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class AvailableHotelDTO {
    private Long hotelId;
    private String hotelName;
    private AddressDTO address;

    private Double minPrice;

    private List<String > availableRoomTypes;
}
