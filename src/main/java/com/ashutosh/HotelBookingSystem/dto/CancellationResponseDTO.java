package com.ashutosh.HotelBookingSystem.dto;

import com.ashutosh.HotelBookingSystem.Enum.BookingStatus;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CancellationResponseDTO {

    private Long bookingId;
    private String cancelledBy;
    private String reason;
    private double originalAmount;
    private double deductionAmount;
    private double refundAmount;
    private String status;
}
