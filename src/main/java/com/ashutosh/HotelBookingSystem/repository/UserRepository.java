package com.ashutosh.HotelBookingSystem.repository;

import com.ashutosh.HotelBookingSystem.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    boolean existsByUniqueIdNumber(String uniqueIdNumber);

    boolean existsByEmail(String email);
    boolean existsByPhoneNo(String phoneNo);


}
