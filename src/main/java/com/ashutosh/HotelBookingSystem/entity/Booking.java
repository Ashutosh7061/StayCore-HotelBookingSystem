package com.ashutosh.HotelBookingSystem.entity;

import com.ashutosh.HotelBookingSystem.Enum.BookingStatus;
import com.ashutosh.HotelBookingSystem.Enum.CancelledBy;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String bookingReferenceId;

    private int numberOfRooms;
    private double totalPrice;
    private String roomType;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private BookingStatus status;

    @JsonFormat(pattern = "MM-dd-yyyy HH:mm:ss")
    private LocalDateTime bookingTime;

    @JsonFormat(pattern = "MM-dd-yyyy")
    private LocalDate checkInDate;
    @JsonFormat(pattern = "MM-dd-yyyy")
    private LocalDate checkOutDate;

    private String checkInInstruction;

    // RelationShip Creation

    @ManyToOne
    @JoinColumn(name = "user_id",nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "hotel_id", nullable = false)
    private Hotel hotel;

    @ElementCollection
    private List<String> allottedRoomNumber;

    // For cancellation

    @Enumerated(EnumType.STRING)
    private CancelledBy cancelledBy;

    private String cancellationReason;

    // for check-out
    private Integer rating;
    private String review;

    @JsonFormat(pattern = "MM-dd-yyyy HH:mm:ss")
    private LocalDateTime checkoutTime;

    private String roomCondition;

}
