package com.ashutosh.HotelBookingSystem.entity;

import com.ashutosh.HotelBookingSystem.Enum.IdType;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "users",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"uniqueIdNumber"})
        }
)
@Getter
@Setter
@NoArgsConstructor
@JsonPropertyOrder(
        {"userId", "name", "email", "phoneNo","address","uniqueIdType","uniqueIdNumber"})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty("userId")
    private Long id;

    private String name;
    @Column(nullable = false, unique = true)
    private String email;

    private String phoneNo;
    private String address;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IdType uniqueIdType;

    @Column(nullable = false, unique = true)
    private String uniqueIdNumber;

}
