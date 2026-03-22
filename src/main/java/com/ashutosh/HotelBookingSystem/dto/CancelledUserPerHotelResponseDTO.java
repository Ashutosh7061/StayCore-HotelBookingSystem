package com.ashutosh.HotelBookingSystem.dto;

import com.ashutosh.HotelBookingSystem.Enum.CancelledBy;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class CancelledUserPerHotelResponseDTO extends BaseUserPerHotelResponseDTO{
    private CancelledBy cancelledBy;
    private String cancellationReason;

    public CancelledUserPerHotelResponseDTO(
            Long userId,
            String name,
            String customerType,
            String email,
            String phoneNo,
            LocalDateTime createdAt,
            CancelledBy cancelledBy,
            String cancellationReason) {

        super(userId, customerType,name, email, phoneNo, createdAt);
        this.cancelledBy = cancelledBy;
        this.cancellationReason = cancellationReason;
    }


}
