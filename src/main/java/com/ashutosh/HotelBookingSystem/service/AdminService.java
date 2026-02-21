package com.ashutosh.HotelBookingSystem.service;

import com.ashutosh.HotelBookingSystem.dto.AdminUserDetailsDTO;
import com.ashutosh.HotelBookingSystem.entity.User;
import com.ashutosh.HotelBookingSystem.repository.BookingRepository;
import com.ashutosh.HotelBookingSystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;


    public List<AdminUserDetailsDTO> getAllUsersForAdmin(){

        List<User> users = userRepository.findAll();

        return users.stream()
                .map(user->{

                    Double totalSpend = bookingRepository.getTotalSpendByUserId(user.getId());

                    Long totalBookings = bookingRepository.getTotalBookingByUserId(user.getId());

                    return new AdminUserDetailsDTO(
                            user.getId(),
                            user.getName(),
                            user.getEmail(),
                            user.getPhoneNo(),
                            user.getCreatedAt(),
                            totalSpend,
                            totalBookings
                    );
                })
                .toList();
    }

}
