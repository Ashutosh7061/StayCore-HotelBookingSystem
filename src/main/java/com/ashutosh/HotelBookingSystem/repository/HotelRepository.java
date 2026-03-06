package com.ashutosh.HotelBookingSystem.repository;

import com.ashutosh.HotelBookingSystem.entity.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HotelRepository extends JpaRepository<Hotel,Long> {
    boolean existsByPhoneNo(String phoneNo);

    boolean existsByHotelNameAndAddressLineAndCityAndPinCode(String hotelName, String addressLine, String city, String pinCode);

    boolean existsByGovtRegisteredNo(String govtRegisteredNo);

    boolean existsByEmail(String email);
}
