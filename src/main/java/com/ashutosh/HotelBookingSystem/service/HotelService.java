package com.ashutosh.HotelBookingSystem.service;

import com.ashutosh.HotelBookingSystem.Enum.BookingStatus;
import com.ashutosh.HotelBookingSystem.Enum.HotelStatus;
import com.ashutosh.HotelBookingSystem.Enum.RoomStatus;
import com.ashutosh.HotelBookingSystem.dto.*;
import com.ashutosh.HotelBookingSystem.entity.Booking;
import com.ashutosh.HotelBookingSystem.entity.Hotel;
import com.ashutosh.HotelBookingSystem.entity.Room;
import com.ashutosh.HotelBookingSystem.exception.*;
import com.ashutosh.HotelBookingSystem.repository.*;
import com.ashutosh.HotelBookingSystem.security.CustomUserDetails;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
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
    private final ReviewRepository reviewRepository;

    @PersistenceContext
    private EntityManager entityManager;

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

    @Transactional(rollbackFor = Exception.class)
    public CheckInResponseDTO checkIn(CheckInRequestDTO request){

        CustomUserDetails loggedUser = (CustomUserDetails) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        Long hotelId = loggedUser.getReferenceId();

        //verifying input
        if(request.getBookingId() == null && request.getBookingReferenceId() == null){
            throw new IllegalArgumentException("Either bookingId or bookingReferenceId must be provided");
        }

        //extracting from database
        Booking booking;
       
        if(request.getBookingId() != null){
            booking = bookingRepository.findById(request.getBookingId())
                    .orElseThrow(() -> new DataNotFoundException("Booking not found with bookingId"));
        } else {
            booking = bookingRepository.findByBookingReferenceId(request.getBookingReferenceId())
                    .orElseThrow(() -> new DataNotFoundException("Booking not found with referenceId"));
        }

        // if both booking id is provided
        if(request.getBookingId() != null && request.getBookingReferenceId() != null){
            if(!booking.getBookingReferenceId().equals(request.getBookingReferenceId())){
                throw new IllegalArgumentException("BookingId and ReferenceId do not match");
            }
        }

        validateHotelOperation(booking.getHotel());

        if(!booking.getHotel().getId().equals(hotelId)){
            throw new UnauthorizedAccessException("You cannot check-in bookings of another hotel");
        }

        // verifying date
        LocalDate today = LocalDate.now();
        LocalDate checkInDate = booking.getCheckInDate();
        LocalDate checkOutDate = booking.getCheckOutDate();

        if(today.isBefore(checkInDate)){
            throw new BookingCheckInException("Check-in not started yet");
        }

        if(today.isEqual(checkOutDate) || today.isAfter(checkOutDate)){
            throw new BookingCheckInException("Check-in not allowed on/after checkout date");
        }

        if(!booking.getUser().getUniqueIdNumber().equals(request.getUniqueIdNumber())){
            throw new InvalidIdNumberException("Invalid ID proof");
        }

        // verifying status
        if(booking.getStatus() == BookingStatus.CANCELLED){
            throw new IllegalStateException("Cannot check-in cancelled booking");
        }
        if(booking.getStatus() == BookingStatus.COMPLETED){
            throw new IllegalStateException("Already checked out");
        }
        if(booking.getStatus() == BookingStatus.CHECKED_IN){
            throw new IllegalStateException("Already checked in");
        }
        if(booking.getStatus() != BookingStatus.CONFIRMED){
            throw new IllegalStateException("Booking not eligible for check-in");
        }

        List<Room> bookedRooms = roomRepository.findAvailableRoomsForUpdate(
                booking.getHotel().getId(),
                booking.getRoomType(),
                RoomStatus.BOOKED
        );

        if(bookedRooms.size() < booking.getNumberOfRooms()){
            throw new IllegalStateException("Rooms not available for check-in");
        }

        List<String> assignedRoom = new ArrayList<>();
        List<Room> assignedRooms = new ArrayList<>();

        for(int i = 0 ; i < booking.getNumberOfRooms(); i++){
            Room room = bookedRooms.get(i);
            room.setStatus(RoomStatus.OCCUPIED);

            assignedRoom.add(room.getRoomNumber());
            assignedRooms.add(room); // ✅ track only assigned rooms
        }

        roomRepository.saveAll(assignedRooms);

        booking.setAllottedRoomNumber(assignedRoom);
        booking.setStatus(BookingStatus.CHECKED_IN);

        return new CheckInResponseDTO(
                booking.getId(),
                booking.getUser().getName(),
                assignedRoom,
                LocalDateTime.now(),
                booking.getCheckInInstruction()
        );
    }



    @Transactional
    public CheckoutResponseDTO checkout(CheckOutRequestDTO request){

        CustomUserDetails loggedUser = (CustomUserDetails) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        Long hotelId = loggedUser.getReferenceId();

//        if(!loggedUser.getRole().equals("HOTEL")){
//            throw new UnauthorizedAccessException("Only hotel can perform checkout");
//        }

        Booking booking;
        if(request.getBookingId() != null){
            booking = bookingRepository.findById(request.getBookingId())
                    .orElseThrow(() -> new DataNotFoundException("Booking not found with bookingId"));
        }
        else{
            booking = bookingRepository.findByBookingReferenceId(request.getBookingReferenceId())
                    .orElseThrow(() -> new DataNotFoundException("Booking not found with referenceId"));
        }

        if(request.getBookingId() != null && request.getBookingReferenceId() != null){
            if(!booking.getBookingReferenceId().equals(request.getBookingReferenceId())){
                throw new IllegalArgumentException("BookingId and ReferenceId do not match");
            }
        }

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
        if(booking.getStatus() != BookingStatus.CHECKED_IN){
            throw new BookingCheckoutException("Only checked-in booking can be checked out");
        }

        booking.setStatus(BookingStatus.COMPLETED);
        booking.setRoomCondition(request.getRoomCondition().toUpperCase());
        booking.setCheckoutTime(LocalDateTime.now());


        List<String> roomNumber = booking.getAllottedRoomNumber();
        List<Room> rooms = roomRepository.findByHotel_Id(booking.getHotel().getId());

        for(Room room : rooms){
            if(roomNumber.contains(room.getRoomNumber())){
                room.setStatus(RoomStatus.VACANT);
            }
        }

        commissionService.addBookingCommission(booking,booking.getHotel());

        return new CheckoutResponseDTO(
                booking.getId(),
                booking.getStatus().name(),
                booking.getRoomCondition(),
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

    public HotelProfileResponseDTO getHotelProfile(){

        CustomUserDetails loggedUser = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Long hotelId = loggedUser.getReferenceId();

        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(()-> new DataNotFoundException("Hotel not found"));

        return mapHotelToProfileDTO(hotel);
    }

    public HotelProfileResponseDTO updateHotelProfile(HotelProfileUpdateDTO request){

        CustomUserDetails loggedUser = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Long hotelId = loggedUser.getReferenceId();

        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(()-> new DataNotFoundException("Hotel not found"));

        if(request.getPhoneNo() != null && !request.getPhoneNo().isBlank()){
            hotel.setPhoneNo(request.getPhoneNo());
        }


        if(request.getAddress() != null){
            if(request.getAddress().getAddressLine() != null){
                hotel.setAddressLine(request.getAddress().getAddressLine());
            }

            if(request.getAddress().getCity() != null){
                hotel.setCity(request.getAddress().getCity());
            }

            if(request.getAddress().getState() != null){
                hotel.setState(request.getAddress().getState());
            }

            if(request.getAddress().getPinCode() != null){
                hotel.setPinCode(request.getAddress().getPinCode());
            }
        }

        hotelRepository.save(hotel);

        return mapHotelToProfileDTO(hotel);
    }

    private HotelProfileResponseDTO mapHotelToProfileDTO(Hotel hotel){

        AddressDTO address = new AddressDTO();
        address.setAddressLine(hotel.getAddressLine());
        address.setCity(hotel.getCity());
        address.setState(hotel.getState());
        address.setPinCode(hotel.getPinCode());

        HotelProfileResponseDTO dto = new HotelProfileResponseDTO();

        dto.setHotelId(hotel.getId());
        dto.setHotelName(hotel.getHotelName());
        dto.setOwnerName(hotel.getRegisteredOwnerName());
        dto.setPhoneNo(hotel.getPhoneNo());
        dto.setEmail(hotel.getEmail());

        dto.setGstNo(hotel.getGstNo());
        dto.setGovtRegisteredNo(hotel.getGovtRegisteredNo());

        dto.setAddress(address);
        dto.setStatus(hotel.getStatus());
        dto.setCreatedAt(hotel.getCreatedAt());

        return dto;
    }
}
