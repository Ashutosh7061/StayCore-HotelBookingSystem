package com.ashutosh.HotelBookingSystem.repository;

import com.ashutosh.HotelBookingSystem.Enum.BookingStatus;
import com.ashutosh.HotelBookingSystem.entity.Hotel;
import com.ashutosh.HotelBookingSystem.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface HotelRepository extends JpaRepository<Hotel,Long> {
    boolean existsByContact(String contact);

    boolean existsByHotelNameAndAddressLineAndCityAndPinCode(String hotelName, String addressLine, String city, String pinCode);

    @Query("""
       SELECT DISTINCT b.user 
       FROM Booking b 
       WHERE b.hotel.id = :hotelId 
       AND b.status = :status
       """)
    List<User> findUsersByHotelAndStatus(Long hotelId, BookingStatus status);
}
