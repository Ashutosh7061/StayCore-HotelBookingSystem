package com.ashutosh.HotelBookingSystem.service;

import com.ashutosh.HotelBookingSystem.Enum.CommissionType;
import com.ashutosh.HotelBookingSystem.entity.Booking;
import com.ashutosh.HotelBookingSystem.entity.Commission;
import com.ashutosh.HotelBookingSystem.entity.Hotel;
import com.ashutosh.HotelBookingSystem.repository.CommissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CommissionService {

    private final CommissionRepository commissionRepository;

    public void  addRoomRegistrationCommissionFee(Hotel hotel){


        Commission commission = new Commission();
        commission.setAmount(100);
        commission.setType(CommissionType.REGISTRATION);
        commission.setHotel(hotel);
        commission.setCreatedAt(LocalDateTime.now());

        commissionRepository.save(commission);
    }

    public void addBookingCommission(Booking booking, Hotel hotel){
        double commissionPercentage = 0.10;
        double commissionAmount = booking.getTotalPrice() * commissionPercentage;

        Commission commission = new Commission();
        commission.setAmount(commissionAmount);
        commission.setType(CommissionType.BOOKING);
        commission.setBooking(booking);
        commission.setCreatedAt(LocalDateTime.now());
        commission.setHotel(hotel);

        commissionRepository.save(commission);
    }
}
