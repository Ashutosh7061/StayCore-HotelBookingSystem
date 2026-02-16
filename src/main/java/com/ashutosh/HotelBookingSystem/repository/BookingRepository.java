package com.ashutosh.HotelBookingSystem.repository;

import com.ashutosh.HotelBookingSystem.Enum.BookingStatus;
import com.ashutosh.HotelBookingSystem.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByUserId(Long userId);

    List<Booking> findByHotelId(Long hotelId);

    List<Booking> findByUser_IdAndHotel_IdAndRoomTypeAndCheckInDateAndCheckOutDateAndStatus(
            Long userId,
            Long hotelId,
            String roomType,
            LocalDate checkInDate,
            LocalDate checkOutDate,
            BookingStatus status
    );

}
