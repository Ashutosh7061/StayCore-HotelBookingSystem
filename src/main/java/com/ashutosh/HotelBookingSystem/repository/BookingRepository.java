package com.ashutosh.HotelBookingSystem.repository;

import com.ashutosh.HotelBookingSystem.Enum.BookingStatus;
import com.ashutosh.HotelBookingSystem.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.awt.print.Book;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

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

    @Query("""
            SELECT COALESCE(SUM(b.totalPrice),0)
            FROM Booking b
            WHERE b.user.id = :userId
            AND b.status IN ('CONFIRMED','COMPLETED')
            """)
    Double getTotalSpendByUserId(Long userId);


    @Query("""
            SELECT COUNT(b)
            FROM Booking b
            WHERE b.user.id = :userId
            AND b.status IN('CONFIRMED','COMPLETED')
            """)
    Long getTotalBookingByUserId(Long userId);

}
