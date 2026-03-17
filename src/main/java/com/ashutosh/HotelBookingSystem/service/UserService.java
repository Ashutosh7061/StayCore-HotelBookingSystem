package com.ashutosh.HotelBookingSystem.service;

import com.ashutosh.HotelBookingSystem.Enum.BookingStatus;
import com.ashutosh.HotelBookingSystem.dto.*;
import com.ashutosh.HotelBookingSystem.entity.Booking;
import com.ashutosh.HotelBookingSystem.entity.User;
import com.ashutosh.HotelBookingSystem.exception.DataNotFoundException;
import com.ashutosh.HotelBookingSystem.exception.DuplicateDataException;
import com.ashutosh.HotelBookingSystem.repository.BookingRepository;
import com.ashutosh.HotelBookingSystem.repository.UserRepository;
import com.ashutosh.HotelBookingSystem.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import com.ashutosh.HotelBookingSystem.Mapper.helperFunctions;



import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    public UserProfileResponseDTO getUserProfile(){

        CustomUserDetails loggedUser = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Long userId = loggedUser.getReferenceId();

        User user = userRepository.findById(userId)
                .orElseThrow(()-> new DataNotFoundException("User not found"));

        return mapUserToProfileDTO(user);
    }

    public UserProfileResponseDTO updateUserProfile(UserProfileUpdateDTO request){

        CustomUserDetails loggedUser = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Long userId = loggedUser.getReferenceId();

        User user = userRepository.findById(userId)
                .orElseThrow(()-> new DataNotFoundException("User not found"));

        if(request.getName() != null){
            user.setName(request.getName());
        }
        if(request.getPhoneNo() != null){
            if(userRepository.existsByPhoneNo(request.getPhoneNo())){
                throw new DuplicateDataException("Phone number already in use");
            }
            helperFunctions.isValidPhoneNumber(request.getPhoneNo());

            user.setPhoneNo(request.getPhoneNo());

        }
        if(request.getAddress() != null){
            user.setAddress(request.getAddress());
        }

        userRepository.save(user);

         return mapUserToProfileDTO(user);
    }

//    @Override
    public UserDashboardDTO getUserDashboard(){

        CustomUserDetails loggedUser = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Long userId = loggedUser.getReferenceId();

        User user = userRepository.findById(userId)
                .orElseThrow(()-> new DataNotFoundException("User not found"));

        List<Booking> bookings = bookingRepository.findBookingsWithHotel(userId);

        //Filling UserInfo
        UserDashboardInfoDTO userInfo = new UserDashboardInfoDTO(
                user.getId(),
                user.getName(),
                user.getEmail()
        );

        //Filling BookingStats
        UserDashboardBookingStatsDTO bookingStats = new UserDashboardBookingStatsDTO();

        bookingStats.setTotalBooking(bookingRepository.countByUser_Id(userId));
        bookingStats.setTotalSpend(bookingRepository.getTotalSpendByUserId(userId));

        bookingStats.setConfirmedBookings(
                (long) bookings.stream()
                        .filter(b->b.getStatus() == BookingStatus.CONFIRMED)
                        .count()
        );

        bookingStats.setCompletedBookings(
                (long)bookings.stream()
                        .filter(b->b.getStatus() == BookingStatus.COMPLETED)
                        .count()
        );

        bookingStats.setCancelledBokings(
                (long)bookings.stream()
                        .filter(b->b.getStatus() == BookingStatus.CANCELLED)
                        .count()
        );

        // Filling RecentBookingsStats
        List<UserDashboardRecentBookingDTO> recentBookingsStats = bookings.stream()
                .limit(3)
                .map(b-> {
                    UserDashboardRecentBookingDTO dto = new UserDashboardRecentBookingDTO(
                            b.getBookingReferenceId(),
                            b.getHotel().getHotelName(),
                            b.getRoomType(),
                            b.getCheckInDate(),
                            b.getCheckOutDate(),
                            b.getStatus(),
                            b.getTotalPrice()
                    );
                    return dto;

                }).toList();

         return new UserDashboardDTO(
                 userInfo,
                 bookingStats,
                 recentBookingsStats
         );
    }

    private UserProfileResponseDTO mapUserToProfileDTO(User user){

        UserProfileResponseDTO dto = new UserProfileResponseDTO();
        dto.setUserId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setPhoneNo(user.getPhoneNo());
        dto.setAddress(user.getAddress());
        dto.setUniqueIdType(user.getUniqueIdType());
        dto.setUniqueIdNumber(user.getUniqueIdNumber());

        return dto;
    }

}
