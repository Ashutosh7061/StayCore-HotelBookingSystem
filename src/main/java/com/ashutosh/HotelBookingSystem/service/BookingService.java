package com.ashutosh.HotelBookingSystem.service;

import com.ashutosh.HotelBookingSystem.Enum.BookingStatus;
import com.ashutosh.HotelBookingSystem.Enum.CancelledBy;
import com.ashutosh.HotelBookingSystem.Enum.RoomStatus;
import com.ashutosh.HotelBookingSystem.dto.*;
import com.ashutosh.HotelBookingSystem.entity.Booking;
import com.ashutosh.HotelBookingSystem.entity.Hotel;
import com.ashutosh.HotelBookingSystem.entity.Room;
import com.ashutosh.HotelBookingSystem.entity.User;
import com.ashutosh.HotelBookingSystem.exception.*;
import com.ashutosh.HotelBookingSystem.repository.BookingRepository;
import com.ashutosh.HotelBookingSystem.repository.HotelRepository;
import com.ashutosh.HotelBookingSystem.repository.RoomRepository;
import com.ashutosh.HotelBookingSystem.repository.UserRepository;
import com.ashutosh.HotelBookingSystem.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ashutosh.HotelBookingSystem.Mapper.helperFunctions;

import java.nio.file.AccessDeniedException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final HotelRepository hotelRepository;
    private final UserRepository userRepository;


    @Transactional
    public BookingResponseDTO bookRoom(UserBookingRequestDTO request){

        CustomUserDetails loggedUser = (CustomUserDetails) SecurityContextHolder.getContext()
                        .getAuthentication()
                        .getPrincipal();


        Long userId = loggedUser.getReferenceId();

        LocalDate checkInDate = request.getCheckInDate();
        LocalDate checkOutDate = request.getCheckOutDate();

        // Duplicate booking check
        List<Booking> existingBookings =
                bookingRepository
                        .findByUser_IdAndHotel_IdAndRoomTypeAndCheckInDateAndCheckOutDateAndStatus(
                                userId,
                                request.getHotelId(),
                                request.getRoomType(),
                                checkInDate,
                                checkOutDate,
                                BookingStatus.CONFIRMED
                        );

        if (!existingBookings.isEmpty() && (request.getDuplicateBooking() == null || !request.getDuplicateBooking())) {
            throw new DuplicateDataException(
                    "Same booking already exists. If you want to book again, pass duplicateBooking=true."
            );
        }

        if(checkOutDate.isBefore(checkInDate)
                || checkOutDate.isEqual(checkInDate)){
            throw new InvalidCheckOutDateException("Invalid check-out date, please provide correct check-out date.");
        }
        if(checkInDate.isBefore(LocalDate.now())){
            throw new InvalidCheckOutDateException("Check-in date must be valid");
        }

        long days = ChronoUnit.DAYS.between(checkInDate, checkOutDate);

        User user = userRepository.findById(userId)
                        .orElseThrow(()-> new DataNotFoundException("User not found."));

        Hotel hotel = hotelRepository.findById(request.getHotelId())
                        .orElseThrow(()-> new DataNotFoundException("Hotel not found"));

        List<Room> availableRooms = roomRepository
                        .findByHotel_IdAndRoomTypeAndStatus(request.getHotelId(), request.getRoomType(), RoomStatus.VACENT);

        if(availableRooms.size() < request.getNoOfRooms()){
            throw new DataNotFoundException("Not enough rooms available, there are only "+ availableRooms.size()+" rooms available");
        }

        //Price calculation
        double pricePerRoom = availableRooms.get(0).getPrice();
        double totalPrice = pricePerRoom * days * request.getNoOfRooms();

        //Updating rooms status
        List<String> allottedRooms = new ArrayList<>();

        for(int i = 0 ; i < request.getNoOfRooms() ; i++){
            Room room = availableRooms.get(i);
            room.setStatus(RoomStatus.BOOKED);
        }

        Booking booking = new Booking();

        booking.setUser(user);
        booking.setHotel(hotel);
        booking.setRoomType(request.getRoomType());
        booking.setNumberOfRooms(request.getNoOfRooms());
        booking.setCheckInDate(checkInDate);
        booking.setCheckOutDate(checkOutDate);
        booking.setTotalPrice(totalPrice);
        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setBookingTime(LocalDateTime.now());
        booking.setAllottedRoomNumber(allottedRooms);

        Booking savedBooking = bookingRepository.save(booking);

        return new BookingResponseDTO(
                savedBooking.getId(),
                hotel.getId(),
                hotel.getHotelName(),
                (int)days,
                request.getNoOfRooms(),
                savedBooking.getCheckInDate(),
                savedBooking.getCheckOutDate(),
                totalPrice,
                savedBooking.getStatus().name(),
                savedBooking.getBookingTime()
        );

    }

    public List<UserBookingResponseDTO> getIndividualUserBookings(BookingStatus status){

        CustomUserDetails loggedUser = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Long userId = loggedUser.getReferenceId();

        userRepository.findById(userId)
                .orElseThrow(() ->
                        new DataNotFoundException(
                                "User not found with id: " + userId
                        )
                );

        List<Booking> bookings;

        if (status == null) {
            bookings = bookingRepository.findBookingsWithHotel(userId);
        } else {
            bookings = bookingRepository.findBookingsWithHotelAndStatus(userId, status);
        }

        if (bookings.isEmpty()) {
            throw new DataNotFoundException("No bookings found for user id: " + userId);
        }
        return bookings.stream()
                .map(booking -> {
                    int days = (int) ChronoUnit.DAYS.between(
                            booking.getCheckInDate(),
                            booking.getCheckOutDate()
                    );
                    AddressDTO addressDTO = new AddressDTO(
                            booking.getHotel().getAddressLine(),
                            booking.getHotel().getCity(),
                            booking.getHotel().getState(),
                            booking.getHotel().getPinCode()
                    );

                    return new UserBookingResponseDTO(
                            booking.getId(),
                            booking.getHotel().getHotelName(),
                            addressDTO,
                            days,
                            booking.getNumberOfRooms(),
                           booking.getStatus().name(),
                            booking.getBookingTime()
                    );
                })
                .toList();
    }


    public List<HotelBookingResponseDTO> getHotelBookings() {

        CustomUserDetails loggedUser = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Long hotelId = loggedUser.getReferenceId();

        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(()-> new DataNotFoundException("Hotel not found with id: "+ hotelId));

        List<Booking> bookings = bookingRepository.findByHotelId(hotelId);

        if(bookings.isEmpty()){
            throw new DataNotFoundException("No booking found for the hotel with id: "+ hotelId);
        }

        return bookings.stream()
                .map(booking -> {

                    int days = (int) ChronoUnit.DAYS.between(
                            booking.getCheckInDate(),
                            booking.getCheckOutDate()
                    );

                    return new HotelBookingResponseDTO(
                            booking.getId(),
                            booking.getUser().getName(),
                            booking.getAllottedRoomNumber(),
                            days,
                            booking.getNumberOfRooms(),
                            booking.getTotalPrice(),
                            booking.getStatus().name(),
                            booking.getBookingTime()
                    );
                })
                .toList();
    }


    public BookingSummaryDTO getBookingSummary(Long bookingId){

        CustomUserDetails loggedUser = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Long userId = loggedUser.getReferenceId();

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(()-> new DataNotFoundException("Booking not found for this bookingId."));


        if(!booking.getUser().getId().equals(userId)){
            throw new UnauthorizedAccessException("You are not allowed to view this bookings.");
        }
        int days = helperFunctions.calculateDays(booking);

        String fullAddress = booking.getHotel().getAddressLine()+ ", "+
                booking.getHotel().getCity()+", "+ booking.getHotel().getState()+", "+
                booking.getHotel().getPinCode();

        return new BookingSummaryDTO(
                booking.getId(),

                booking.getHotel().getId(),
                booking.getHotel().getHotelName(),
                fullAddress,
                booking.getAllottedRoomNumber(),
                days,
                booking.getNumberOfRooms(),
                booking.getTotalPrice(),
                booking.getStatus().name(),
                booking.getBookingTime()
        );
    }


    public List<BaseUserPerHotelResponseDTO> getAllUserOfHotelByStatus(Long hotelId, BookingStatus status){

        hotelRepository.findById(hotelId)
                .orElseThrow(() ->
                        new DataNotFoundException("Hotel not found with id: " + hotelId)
                );

        List<Booking> bookings = bookingRepository.findBookingsByHotelAndStatus(hotelId, status);

        if (bookings.isEmpty()) {
            throw new DataNotFoundException(
                    "No booking found for status " + status
            );
        }
        return bookings.stream()
                .map(booking -> {

                    User user = booking.getUser();

                    if (status == BookingStatus.CANCELLED) {

                        return new CancelledUserPerHotelResponseDTO(
                                user.getId(),
                                user.getName(),
                                user.getEmail(),
                                user.getPhoneNo(),
                                user.getCreatedAt(),
                                booking.getCancelledBy(),
                                booking.getCancellationReason()
                        );
                    }

                    if (status == BookingStatus.COMPLETED) {

                        return new CompletedUserPerHotelResponseDTO(
                                user.getId(),
                                user.getName(),
                                user.getEmail(),
                                user.getPhoneNo(),
                                user.getCreatedAt(),
                                booking.getReview(),
                                booking.getRating()
                        );
                    }

                    // CONFIRMED or others
                    return new BaseUserPerHotelResponseDTO(
                            user.getId(),
                            user.getName(),
                            user.getEmail(),
                            user.getPhoneNo(),
                            user.getCreatedAt()
                    );

                })
                .toList();
    }


    @Transactional
    public CancellationResponseDTO cancelBooking(Long bookingId, CancelledBy cancelledBy, String reason){

        CustomUserDetails loggedUser = (CustomUserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        String role = loggedUser.getRole();
        Long referenceId = loggedUser.getReferenceId();

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(()-> new DataNotFoundException("Booking not found for this given id."));


        if(role.equals("USER")){
            if(!booking.getUser().getId().equals(referenceId)){
                throw new UnauthorizedAccessException("You can cancel yours own bookings.");
            }
            if(cancelledBy != CancelledBy.USER){
                throw new BookingValidationException("Invalid cancellation type for user");
            }
        }
        else if(role.equals("HOTEL")){
            if(!booking.getHotel().getId().equals(referenceId)){
                throw new UnauthorizedAccessException("You can cancel only booking of your own hotel");
            }
            if(cancelledBy != CancelledBy.HOTEL){
                throw new BookingValidationException("Invalid cancellation type for hotel");
            }
        }
        if(booking.getStatus() == BookingStatus.CANCELLED){
            throw new InvalidBookingStateException("Booking already cancelled");
        }

        if(booking.getStatus() == BookingStatus.COMPLETED){
            throw new InvalidBookingStateException("Completed booking cannot be cancelled");
        }

        double deduction = 0;
        double refund = booking.getTotalPrice();

        if(cancelledBy == CancelledBy.USER){

            LocalDate today = LocalDate.now();
            LocalDate checkInDate = booking.getCheckInDate();

            long daysBetween = ChronoUnit.DAYS.between(today, checkInDate);
            double deductionPercentage;

            if(daysBetween < 0){
                throw new InvalidBookingStateException("Cannot cancel after check-in date");
            }

            if(daysBetween <= 0){
                deductionPercentage = 0.50;
            }
            else if(daysBetween <= 3){
                deductionPercentage = 0.15;
            }
            else{
                deductionPercentage = 0.0;
            }

            double totalPrice = booking.getTotalPrice();
            deduction = totalPrice * deductionPercentage;
            refund = totalPrice - deduction;

            booking.setCancellationReason(null);
        }

        if(cancelledBy == CancelledBy.HOTEL){
            if(reason == null || reason.isBlank()){
                throw new BookingValidationException("Cancellation reason requires when hotel cancels booking");
            }
            deduction = 0;
            refund = booking.getTotalPrice();
            booking.setCancellationReason(reason);
            //In future credits to be added
        }

        booking.setStatus(BookingStatus.CANCELLED);
        booking.setCancelledBy(cancelledBy);

        List<String> roomNumbers = booking.getAllottedRoomNumber();

        List<Room> rooms = roomRepository.findByHotel_Id(booking.getHotel().getId());

        for(Room room : rooms){
            if(roomNumbers.contains(room.getRoomNumber())){
                room.setStatus(RoomStatus.VACENT);
            }
        }
        return new CancellationResponseDTO(
                booking.getId(),
                cancelledBy.name(),
                reason,
                booking.getTotalPrice(),
                deduction,
                refund,
                booking.getStatus().name()
        );
    }

    public List<AdminBookingDetailsDTO> getCompletedBookingForAdmin(){

        List<Booking> bookings = bookingRepository.findByStatus(BookingStatus.COMPLETED);

        return bookings.stream()
                .map(booking -> new AdminBookingDetailsDTO(
                     booking.getId(),
                     booking.getUser().getId(),
                     booking.getUser().getName(),
                     booking.getHotel().getId(),
                     booking.getHotel().getHotelName(),
                     booking.getTotalPrice(),
                     booking.getStatus(),
                     booking.getRating(),
                     booking.getReview()
                ))
                .toList();
    }
}
