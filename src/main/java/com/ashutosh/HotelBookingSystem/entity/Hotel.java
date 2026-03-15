package com.ashutosh.HotelBookingSystem.entity;


import com.ashutosh.HotelBookingSystem.Enum.HotelStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@JsonPropertyOrder(
        {"id","hotelName","addressLine","city","state","pinCode","phoneNo","rooms"}
)
@Table(
        uniqueConstraints = {
                @UniqueConstraint(name = "UK_HOTEL_LOCATION", columnNames = {
                        "hotel_name", "address_line","city","pin_code"}),
                @UniqueConstraint(name = "UK_HOTEL_CONTACT",
                        columnNames = {"contact"})
        }
)
public class Hotel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty("hotelId")
    private Long id;

    @Column(length = 100)
    private String hotelName;

    @Column(length = 150)
    private String addressLine;

    @Column(length = 80)
    private String city;

    @Column(length = 50)
    private String state;

    @Column(length = 10)
    private String pinCode;

    @Column(length = 12, name = "phoneNo")
    private String phoneNo;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String registeredOwnerName;

    @Column(nullable = false,unique = true)
    private String govtRegisteredNo;

    @Column(nullable = false,unique = true)
    private String gstNo;

    @OneToMany(mappedBy = "hotel")
    private List<Room> rooms;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private HotelStatus status;

    @JsonFormat(pattern = "MM-dd-yyyy HH:mm:ss")
    private LocalDateTime createdAt;

    @PrePersist
    public void setCreatedAt(){
        this.createdAt = LocalDateTime.now();
    }

    private String rejectionReason;

}
