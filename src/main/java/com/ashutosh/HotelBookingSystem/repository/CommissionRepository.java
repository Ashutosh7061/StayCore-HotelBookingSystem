package com.ashutosh.HotelBookingSystem.repository;

import com.ashutosh.HotelBookingSystem.Enum.CommissionType;
import com.ashutosh.HotelBookingSystem.entity.Commission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommissionRepository extends JpaRepository<Commission , Long> {

    @Query("""
       SELECT COALESCE(SUM(c.amount),0)
       FROM Commission c
       WHERE c.hotel.id = :hotelId
       """)
    Double getTotalCommissionByHotelId(Long hotelId);

    @Query("""
       SELECT COALESCE(SUM(c.amount),0)
       FROM Commission c
       WHERE c.hotel.id = :hotelId
       AND c.type = :type
       """)
    Double getCommissionByHotelIdAndType(Long hotelId, CommissionType type);


    @Query("""
       SELECT COALESCE(SUM(c.amount),0)
       FROM Commission c
       WHERE c.type = :type
       """)
    Double getTotalByType(CommissionType type);
}
