package com.ashutosh.HotelBookingSystem.entity;

import com.ashutosh.HotelBookingSystem.Enum.BookingStatus;
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

//    private int numberOfDays;

    private int numberOfRooms;

    private double totalPrice;

    private String roomType;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private BookingStatus status;

    private LocalDateTime bookingTime;


    private LocalDate checkInDate;
    private LocalDate checkOutDate;

    // RelationShip Creation

    @ManyToOne
    @JoinColumn(name = "user_id",nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "hotel_id", nullable = false)
    private Hotel hotel;

    @ElementCollection
    private List<String> allottedRoomNumber;

}
