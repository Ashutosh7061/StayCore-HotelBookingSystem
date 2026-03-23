package com.ashutosh.HotelBookingSystem.repository;

import com.ashutosh.HotelBookingSystem.Enum.HotelStatus;
import com.ashutosh.HotelBookingSystem.entity.Hotel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HotelRepository extends JpaRepository<Hotel,Long> {

    boolean existsByPhoneNo(String phoneNo);

    boolean existsByHotelNameAndAddressLineAndCityAndPinCode(String hotelName, String addressLine, String city, String pinCode);

    boolean existsByGovtRegisteredNo(String govtRegisteredNo);

    boolean existsByEmail(String email);

    List<Hotel> findByStatus(HotelStatus status);

    long countByStatus(HotelStatus hotelStatus);

    Page<Hotel> findByStatus(HotelStatus status, Pageable pageable);
}
