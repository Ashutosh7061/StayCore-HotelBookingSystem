package com.ashutosh.HotelBookingSystem.repository;

import com.ashutosh.HotelBookingSystem.Enum.BookingSource;
import com.ashutosh.HotelBookingSystem.Enum.BookingStatus;
import com.ashutosh.HotelBookingSystem.dto.BookingStatsProjection;
import com.ashutosh.HotelBookingSystem.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByUserId(Long userId);

    @Query("""
       SELECT b
       FROM Booking b
       JOIN FETCH b.hotel
       WHERE b.user.id = :userId
       """)
    List<Booking> findBookingsWithHotel(Long userId);

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

    @Query("""
       SELECT b
       FROM Booking b
       JOIN FETCH b.hotel
       WHERE b.user.id = :userId
       AND b.status = :status
       """)
    List<Booking> findBookingsWithHotelAndStatus(Long userId, BookingStatus status);

    @Query("""
       SELECT b
       FROM Booking b
       WHERE b.hotel.id = :hotelId
       AND b.status = :status
       """)
    List<Booking> findBookingsByHotelAndStatus(Long hotelId, BookingStatus status);

    List<Booking> findByStatus(BookingStatus status);

    long countByStatus(BookingStatus status);

    long countByHotelId(Long hotelId);

    long countByHotelIdAndStatus(Long hotelId, BookingStatus status);

    List<Booking> findByHotel_Id(Long hotelId);

    @Query("""
        SELECT COALESCE(SUM(b.totalPrice),0)
        FROM Booking b
        WHERE b.hotel.id = :hotelId
    """)
    Double getTotalBookingRevenue(Long hotelId);

    Optional<Booking> findByBookingReferenceId(String bookingReferenceId);

    long countByUser_Id(Long userId);

    @Query("""
    SELECT b FROM Booking b
    LEFT JOIN b.user u
    WHERE b.hotel.id = :hotelId
    AND b.roomType = :roomType
    AND b.status IN ('CONFIRMED','CHECKED_IN')
    AND (
        u.uniqueIdNumber = :idNumber
        OR b.guestIdNumber = :idNumber
    )
    AND (
        :checkInDate < b.checkOutDate AND :checkOutDate > b.checkInDate
    )
    """)
    List<Booking> findDuplicateByIdentity(Long hotelId, String roomType,
            LocalDate checkInDate, LocalDate checkOutDate, String idNumber
    );

    long countByBookingSource(BookingSource bookingSource);

    long countByHotel_IdAndBookingSource(Long hotelId, BookingSource bookingSource);

    @Query("""
    SELECT 
        COUNT(b) as totalBookings,

        SUM(CASE WHEN b.status = 'CONFIRMED' THEN 1 ELSE 0 END) as confirmedBookings,
        SUM(CASE WHEN b.status = 'COMPLETED' THEN 1 ELSE 0 END) as completedBookings,
        SUM(CASE WHEN b.status = 'CANCELLED' THEN 1 ELSE 0 END) as cancelledBookings,

        SUM(CASE WHEN b.bookingSource = 'ONLINE' THEN 1 ELSE 0 END) as onlineBookings,
        SUM(CASE WHEN b.bookingSource = 'OFFLINE' THEN 1 ELSE 0 END) as offlineBookings

    FROM Booking b
""")
    BookingStatsProjection getBookingStats();

}
