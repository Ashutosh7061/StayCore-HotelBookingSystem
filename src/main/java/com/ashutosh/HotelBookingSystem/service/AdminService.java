package com.ashutosh.HotelBookingSystem.service;

import com.ashutosh.HotelBookingSystem.Enum.BookingStatus;
import com.ashutosh.HotelBookingSystem.Enum.CommissionType;
import com.ashutosh.HotelBookingSystem.dto.AdminHotelDetailsDTO;
import com.ashutosh.HotelBookingSystem.dto.AdminUserDetailsDTO;
import com.ashutosh.HotelBookingSystem.dto.PlatformDashboardDTO;
import com.ashutosh.HotelBookingSystem.entity.Hotel;
import com.ashutosh.HotelBookingSystem.entity.User;
import com.ashutosh.HotelBookingSystem.exception.DataNotFoundException;
import com.ashutosh.HotelBookingSystem.repository.BookingRepository;
import com.ashutosh.HotelBookingSystem.repository.CommissionRepository;
import com.ashutosh.HotelBookingSystem.repository.HotelRepository;
import com.ashutosh.HotelBookingSystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final CommissionRepository commissionRepository;
    private final HotelRepository hotelRepository;


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

    public AdminHotelDetailsDTO getAdminHotelDetails(Long hotelId) {

        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new DataNotFoundException("Hotel not found"));

        Double totalCommission =
                commissionRepository.getTotalCommissionByHotelId(hotelId);

        Double bookingCommission = commissionRepository.getCommissionByHotelIdAndType(
                        hotelId, CommissionType.BOOKING);

        Double registrationCommission =
                commissionRepository.getCommissionByHotelIdAndType(
                        hotelId, CommissionType.REGISTRATION);

        return new AdminHotelDetailsDTO(
                hotel.getId(),
                hotel.getHotelName(),
                hotel.getAddressLine(),
                hotel.getContact(),
                totalCommission,
                bookingCommission,
                registrationCommission
        );
    }

    public PlatformDashboardDTO getPlatformDashboard() {

        Long totalCompletedBookings =
                bookingRepository.countByStatus(BookingStatus.COMPLETED);

        Double registrationCommission =
                commissionRepository.getTotalByType(CommissionType.REGISTRATION);

        Double bookingCommission =
                commissionRepository.getTotalByType(CommissionType.BOOKING);

        Double totalPlatformEarnings =
                registrationCommission + bookingCommission;

        return new PlatformDashboardDTO(
                totalCompletedBookings,
                registrationCommission,
                bookingCommission,
                totalPlatformEarnings
        );
    }

}
