package com.ashutosh.HotelBookingSystem.entity;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@JsonPropertyOrder(
        {"id","hotelName","addressLine","city","state","pinCode","contact","rooms"}
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

    @Column(length = 12, name = "contact")
    private String contact;

    @OneToMany(mappedBy = "hotel")
    private List<Room> rooms;

}
