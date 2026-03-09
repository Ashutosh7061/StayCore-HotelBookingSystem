package com.ashutosh.HotelBookingSystem.entity;

import com.ashutosh.HotelBookingSystem.Enum.Role;
import com.ashutosh.HotelBookingSystem.Enum.SupportStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class SupportRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private Role role;

    private Long referenceId;

    private String requesterName;
    private String requesterEmail;

//    @Column(length = 50)
    private String message;

    @Enumerated(EnumType.STRING)
    private SupportStatus status;

    private LocalDateTime createdAt;

    @PrePersist
    public void setDefaults(){
        this.createdAt = LocalDateTime.now();
        this.status = SupportStatus.OPEN;
    }


}
