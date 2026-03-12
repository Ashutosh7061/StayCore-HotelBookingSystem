package com.ashutosh.HotelBookingSystem.repository;

import com.ashutosh.HotelBookingSystem.entity.SupportMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SupportMessageRepository extends JpaRepository<SupportMessage, Long> {

    List<SupportMessage> findBySupportRequestId(Long supportRequestId);

    List<SupportMessage> findBySupportRequestIdOrderByCreatedAtAsc(Long supportRequestId);
}
