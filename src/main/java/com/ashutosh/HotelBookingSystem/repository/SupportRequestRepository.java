package com.ashutosh.HotelBookingSystem.repository;

import com.ashutosh.HotelBookingSystem.Enum.SupportStatus;
import com.ashutosh.HotelBookingSystem.entity.SupportRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SupportRequestRepository extends JpaRepository<SupportRequest, Long> {

    List<SupportRequest> findByStatus(SupportStatus status);
}
