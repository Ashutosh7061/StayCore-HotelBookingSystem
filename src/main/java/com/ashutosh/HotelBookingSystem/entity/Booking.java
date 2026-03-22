package com.ashutosh.HotelBookingSystem.entity;

import com.ashutosh.HotelBookingSystem.Enum.BookingSource;
import com.ashutosh.HotelBookingSystem.Enum.BookingStatus;
import com.ashutosh.HotelBookingSystem.Enum.IdType;
import com.fasterxml.jackson.annotation.JsonFormat;
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
    @JoinColumn(name = "user_id",nullable = true)
    private User user;

    @ManyToOne
    @JoinColumn(name = "hotel_id", nullable = false)
    private Hotel hotel;

    @ElementCollection
    private List<String> allottedRoomNumber;

    @JsonFormat(pattern = "MM-dd-yyyy HH:mm:ss")
    private LocalDateTime checkoutTime;

    private String roomCondition;

    //for offline
    @Column(nullable = false)
    private String guestName;
    @Column(nullable = false)
    private String guestPhoneNo;
    @Column(nullable = false)
    private String guestAddress;
    private String guestEmail;

    @Enumerated(EnumType.STRING)
    private IdType guestIdType;

    @Column(nullable = false)
    private String guestIdNumber;

    @Enumerated(EnumType.STRING)
    private BookingSource bookingSource;

    private Boolean isWalkIn;


}
