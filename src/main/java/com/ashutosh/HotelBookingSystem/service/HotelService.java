package com.ashutosh.HotelBookingSystem.service;

import com.ashutosh.HotelBookingSystem.Enum.BookingStatus;
import com.ashutosh.HotelBookingSystem.Enum.HotelStatus;
import com.ashutosh.HotelBookingSystem.Enum.RoomStatus;
import com.ashutosh.HotelBookingSystem.Mapper.helperFunctions;
import com.ashutosh.HotelBookingSystem.dto.*;
import com.ashutosh.HotelBookingSystem.entity.Booking;
import com.ashutosh.HotelBookingSystem.entity.Hotel;
import com.ashutosh.HotelBookingSystem.entity.Room;
import com.ashutosh.HotelBookingSystem.exception.*;
import com.ashutosh.HotelBookingSystem.repository.BookingRepository;
import com.ashutosh.HotelBookingSystem.repository.CommissionRepository;
import com.ashutosh.HotelBookingSystem.repository.HotelRepository;
import com.ashutosh.HotelBookingSystem.repository.RoomRepository;
import com.ashutosh.HotelBookingSystem.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HotelService {

    private final HotelRepository hotelRepository;
    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final CommissionService commissionService;
    private final CommissionRepository commissionRepository;

    public List<HotelResponseDTO> getAllHotels() {
        List<Hotel> hotels = hotelRepository.findAll();

        return hotels.stream()
                .map(hotel -> {

                    AddressDTO addressDTO = new AddressDTO(
                            hotel.getAddressLine(),
                            hotel.getCity(),
                            hotel.getState(),
                            hotel.getPinCode()
                    );
                    return new HotelResponseDTO(
                            hotel.getId(),
                            hotel.getHotelName(),
                            addressDTO,
                            hotel.getPhoneNo(),
                            hotel.getCreatedAt(),
                            hotel.getStatus()
                    );
                })
                .toList();
    }


    public Hotel getHotelWithId(Long hotelId){

        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(()-> new DataNotFoundException("Hotel not found for this id"));

        return hotel;
    }

    @Transactional
    public CheckInResponseDTO checkIn(CheckInRequestDTO request){

        CustomUserDetails loggedUser = (CustomUserDetails) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        String role = loggedUser.getRole();
        Long hotelId = loggedUser.getReferenceId();

        Booking booking = bookingRepository.findById(request.getBookingId())
                .orElseThrow(()-> new DataNotFoundException("Booking not found."));

        validateHotelOperation(booking.getHotel());

        if(!booking.getHotel().getId().equals(hotelId)){
            throw new UnauthorizedAccessException("You cannot check-in bookings of another hotel");
        }

        LocalDate today = LocalDate.now();
        LocalDate checkInDate = booking.getCheckInDate();

        if(today.isBefore(checkInDate)|| today.isAfter(checkInDate) ){
            throw new BookingCheckInException("Check-in is allowed only on check-in date or between booking dates.");
        }
        // verifying user identity
        if(booking.getBookingReferenceId().equals(request.getBookingReferenceId())){
            throw new IllegalArgumentException("Booking reference mismatched");
        }
        if(!booking.getUser().getUniqueIdNumber().equals(request.getUniqueIdNumber())){
            throw new InvalidIdNumberException("Invalid ID proof");
        }
        if(booking.getStatus() != BookingStatus.CONFIRMED){
            throw new IllegalStateException("Booking not eligible for check-in");
        }
        if(!booking.getAllottedRoomNumber().isEmpty()){
            throw new IllegalStateException("Already checked in");
        }

        List<Room> bookedRooms = roomRepository.findByHotel_IdAndRoomTypeAndStatus(
                booking.getHotel().getId(),
                booking.getRoomType(),
                RoomStatus.BOOKED
        );

        if(bookedRooms.size() < booking.getNumberOfRooms()){
            throw new IllegalStateException("Rooms not available for check-in");
        }

        List<String> assignedRoom = new ArrayList<>();

        for(int i = 0 ; i < booking.getNumberOfRooms(); i++){
            Room room = bookedRooms.get(i);
            room.setStatus(RoomStatus.OCCUPIED);
            assignedRoom.add(room.getRoomNumber());
        }

        booking.setAllottedRoomNumber(assignedRoom);

        return new CheckInResponseDTO(
                booking.getId(),
                booking.getUser().getName(),
                assignedRoom,
                LocalDateTime.now()
        );
    }

    @Transactional
    public CheckoutResponseDTO checkout(CheckOutRequestDTO request){

        CustomUserDetails loggedUser = (CustomUserDetails) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        Long hotelId = loggedUser.getReferenceId();

        if(!loggedUser.getRole().equals("HOTEL")){
            throw new UnauthorizedAccessException("Only hotel can perform checkout");
        }

        Booking booking = bookingRepository.findById(request.getBookingId())
                .orElseThrow(()-> new DataNotFoundException("Booking not found with this bookingId: "+ request.getBookingId()));

        validateHotelOperation(booking.getHotel());

        if(!booking.getHotel().getId().equals(hotelId)){
            throw new UnauthorizedAccessException("You cannot checkout bookings of another hotel");
        }

        if(booking.getStatus() == BookingStatus.CANCELLED){
            throw new BookingCheckoutException("Cancelled booking cannot be checked out.");
        }
        if(booking.getStatus() == BookingStatus.COMPLETED){
            throw new BookingCheckoutException("This booking was already checked out.");
        }
        if(booking.getStatus() != BookingStatus.CONFIRMED){
            throw new BookingCheckoutException("Only confirmed booking can be checked out");
        }
        if(request.getRating() != null){
            if(request.getRating() < 1 || request.getRating() > 5){
                throw new InvalidRatingException("Rating must be between 1 and 5.");
            }
        }

        booking.setStatus(BookingStatus.COMPLETED);
        booking.setReview(request.getReview());
        booking.setRating(request.getRating());
        booking.setRoomCondition(request.getRoomCondition().toUpperCase());
        booking.setCheckoutTime(LocalDateTime.now());

        List<String> roomNumber = booking.getAllottedRoomNumber();
        List<Room> rooms = roomRepository.findByHotel_Id(booking.getHotel().getId());

        for(Room room : rooms){
            if(roomNumber.contains(room.getRoomNumber())){
                room.setStatus(RoomStatus.VACENT);
            }
        }

        commissionService.addBookingCommission(booking,booking.getHotel());


        return new CheckoutResponseDTO(
                booking.getId(),
                booking.getStatus().name(),
                booking.getRoomCondition(),
                booking.getRating(),
                booking.getReview(),
                booking.getCheckoutTime()
        );
    }

    public void validateHotelOperation(Hotel hotel){

        if(hotel.getStatus() != HotelStatus.APPROVED){
            throw new UnauthorizedAccessException("Hotel is not approved for doing activities");
        }
//        if(hotel.getStatus() == HotelStatus.PENDING){
//            throw new UnauthorizedAccessException("Hotel account is pending approval by admin");
//        }
//
//        if(hotel.getStatus() == HotelStatus.REJECTED){
//            throw new UnauthorizedAccessException("Hotel application was rejected. Please reapply");
//        }
//
//        if(hotel.getStatus() == HotelStatus.BLOCKED){
//            throw new UnauthorizedAccessException("Hotel account has been blocked by platform");
//        }
    }

    public List<Hotel> getAllAvailableHotels() {
        return hotelRepository.findByStatus(HotelStatus.APPROVED);
    }

    @Transactional
    public String reapplyHotel(){
        CustomUserDetails loggedUser = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Long hotelId = loggedUser.getReferenceId();

        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(()-> new DataNotFoundException("Hotel not found"));

        if(hotel.getStatus() != HotelStatus.REJECTED){
            throw new HotelRegisterException("Only rejected hotels can reapply");
        }

        hotel.setStatus(HotelStatus.PENDING);
        hotel.setRejectionReason(null);

        return "Hotel reapplied successfully. Waiting for admin approval";
    }


    public HotelDashboardDTO getHotelDashboard(){
        CustomUserDetails loggedUser = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Long hotelId = loggedUser.getReferenceId();

        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(()-> new DataNotFoundException("Hotel not found"));

        Long totalRooms = roomRepository.countByHotel_Id(hotelId);

        Long totalBooking = (Long)bookingRepository.countByHotelId(hotelId);
        long completedBookings = bookingRepository.countByHotelIdAndStatus(hotelId,BookingStatus.COMPLETED);
        long cancelledBooking = bookingRepository.countByHotelIdAndStatus(hotelId, BookingStatus.CANCELLED);
        long confirmedBookings  = bookingRepository.countByHotelIdAndStatus(hotelId, BookingStatus.CONFIRMED);


        Double totalBookingRevenue = bookingRepository.getTotalBookingRevenue(hotelId);
        Double totalCommission = commissionRepository.getTotalCommissionByHotelId(hotelId);
        Double netEarning = totalBookingRevenue - totalCommission;

        HotelDashboardInfoDTO hotelInfo = new HotelDashboardInfoDTO(
                hotel.getId(),
                hotel.getHotelName(),
                hotel.getStatus(),
                hotel.getCreatedAt()
        );

        HotelDashboardRoomStatsDTO roomInfo = new HotelDashboardRoomStatsDTO(
                totalRooms
        );

        HotelDashboardBookingStatsDTO bookingInfo = new HotelDashboardBookingStatsDTO(
                totalBooking,
                confirmedBookings,
                completedBookings,
                cancelledBooking
        );

        HotelDashboardFinancialStatsDTO financialInfo = new HotelDashboardFinancialStatsDTO(
                totalBookingRevenue,
                totalCommission,
                netEarning
        );

        return new HotelDashboardDTO(
                hotelInfo,
                roomInfo,
                bookingInfo,
                financialInfo
        );
    }
}
