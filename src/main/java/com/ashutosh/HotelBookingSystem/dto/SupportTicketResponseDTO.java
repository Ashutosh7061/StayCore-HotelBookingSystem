package com.ashutosh.HotelBookingSystem.dto;

import com.ashutosh.HotelBookingSystem.Enum.Role;
import com.ashutosh.HotelBookingSystem.Enum.SupportStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SupportTicketResponseDTO {

    private String tokenId;
    private SupportStatus status;
    private LocalDateTime createdAt;

    private List<MessageDTO> message;


    @Data
    public static class MessageDTO{
        private Role senderRole;
        private String message;
        private LocalDateTime createdAt;
    }
}
