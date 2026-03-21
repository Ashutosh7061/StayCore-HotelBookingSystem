package com.ashutosh.HotelBookingSystem.repository;

import com.ashutosh.HotelBookingSystem.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    boolean existsByBooking_Id(Long bookingId);

    @Query("""
        SELECT COALESCE(AVG(r.rating),0)
        FROM Review r
        WHERE r.hotel.id = :hotelId
    """)
    Double getAverageRatingByHotel(Long hotelId);

    Optional<Review> findByBooking_Id(Long bookingId);


}
