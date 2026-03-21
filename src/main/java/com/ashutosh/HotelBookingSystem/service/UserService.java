package com.ashutosh.HotelBookingSystem.service;

import com.ashutosh.HotelBookingSystem.Enum.BookingStatus;
import com.ashutosh.HotelBookingSystem.dto.*;
import com.ashutosh.HotelBookingSystem.entity.Booking;
import com.ashutosh.HotelBookingSystem.entity.CancellationTransaction;
import com.ashutosh.HotelBookingSystem.entity.Review;
import com.ashutosh.HotelBookingSystem.entity.User;
import com.ashutosh.HotelBookingSystem.exception.*;
import com.ashutosh.HotelBookingSystem.repository.BookingRepository;
import com.ashutosh.HotelBookingSystem.repository.CancellationTransactionRepository;
import com.ashutosh.HotelBookingSystem.repository.ReviewRepository;
import com.ashutosh.HotelBookingSystem.repository.UserRepository;
import com.ashutosh.HotelBookingSystem.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import com.ashutosh.HotelBookingSystem.Mapper.helperFunctions;


import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CancellationTransactionRepository cancellationTransactionRepository;
    private final ReviewRepository reviewRepository;

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
                .sorted((a, b) -> b.getBookingTime().compareTo(a.getBookingTime()))
                .map(b -> {

                    UserDashboardRecentBookingDTO dto = new UserDashboardRecentBookingDTO();

                    dto.setBookingReferenceId(b.getBookingReferenceId());
                    dto.setHotelName(b.getHotel().getHotelName());
                    dto.setRoomType(b.getRoomType());
                    dto.setCheckInDate(b.getCheckInDate());
                    dto.setCheckOutDate(b.getCheckOutDate());
                    dto.setStatus(b.getStatus());
                    dto.setTotalPrice(b.getTotalPrice());

                    if (b.getStatus() == BookingStatus.CANCELLED) {

                        CancellationTransaction tx =
                                cancellationTransactionRepository
                                        .findByBookingId(b.getId())
                                        .orElse(null);

                        if (tx != null) {
                            dto.setRefundAmount(tx.getRefundAmount());
                            dto.setDeductionAmount(tx.getDeductionAmount());
                        }
                    }

                    return dto;

                }).toList();

         return new UserDashboardDTO(
                 userInfo,
                 bookingStats,
                 recentBookingsStats
         );
    }

    public ReviewResponseDTO addReview(ReviewRequestDTO request){

        CustomUserDetails loggedUser = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Long userId = loggedUser.getReferenceId();

        Booking booking = bookingRepository.findById(request.getBookingId())
                .orElseThrow(()-> new DataNotFoundException("Booking not found"));


        if(!booking.getUser().getId().equals(userId)){
            throw new UnauthorizedAccessException("You can only review your own bookings");
        }

        if(booking.getStatus() != BookingStatus.COMPLETED){
            throw new InvalidOperationException("You can review only after checkout");
        }

        if(reviewRepository.existsByBooking_Id(booking.getId())){
            throw new InvalidOperationException("Review already submitted");
        }

        if(request.getRating() == null || request.getRating() < 1 || request.getRating() > 5){
            throw new InvalidRatingException("Rating must be between 1 and 5");
        }

        Review review =new Review();
        review.setBooking(booking);
        review.setUser(booking.getUser());
        review.setHotel(booking.getHotel());
        review.setRating(request.getRating());
        review.setComment(review.getComment());
        review.setCreatedAt(LocalDateTime.now());

        reviewRepository.save(review);

        return new ReviewResponseDTO(
                review.getId(),
                booking.getId(),
                review.getRating(),
                review.getComment(),
                review.getCreatedAt()
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
