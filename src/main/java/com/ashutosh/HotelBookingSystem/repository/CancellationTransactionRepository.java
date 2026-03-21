package com.ashutosh.HotelBookingSystem.repository;

import com.ashutosh.HotelBookingSystem.entity.CancellationTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CancellationTransactionRepository extends JpaRepository<CancellationTransaction, Long> {

    Optional<CancellationTransaction> findByBookingId(Long bookingId);
}
