package com.ashutosh.HotelBookingSystem.entity;

import com.ashutosh.HotelBookingSystem.Enum.CancelledBy;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class CancellationTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long bookingId;

    @Column(nullable = false,unique = true)
    private String bookingReferenceId;

    private double originalAmount;

    private double deductionAmount;

    private double refundAmount;

    private double deductionPercentage;

    @Enumerated(EnumType.STRING)
    private CancelledBy cancelledBy;

    private String cancellationReason;

    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime cancellationTime;
}
