package com.ashutosh.HotelBookingSystem.service;

import com.ashutosh.HotelBookingSystem.Enum.BookingStatus;
import com.ashutosh.HotelBookingSystem.Enum.RoomStatus;
import com.ashutosh.HotelBookingSystem.Mapper.helperFunctions;
import com.ashutosh.HotelBookingSystem.dto.*;
import com.ashutosh.HotelBookingSystem.entity.Booking;
import com.ashutosh.HotelBookingSystem.entity.Hotel;
import com.ashutosh.HotelBookingSystem.entity.Room;
import com.ashutosh.HotelBookingSystem.exception.*;
import com.ashutosh.HotelBookingSystem.repository.BookingRepository;
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

    public Hotel registerHotel(Hotel hotel){

        helperFunctions.isValidPhoneNumber(hotel.getPhoneNo());

        boolean phoneExists = hotelRepository.existsByPhoneNo(hotel.getPhoneNo());

        boolean locationExists = hotelRepository.existsByHotelNameAndAddressLineAndCityAndPinCode(
                hotel.getHotelName(),
                hotel.getAddressLine(),
                hotel.getCity(),
                hotel.getPinCode());

        if(phoneExists && locationExists){
            throw new DuplicateDataException("Phone number and hotel location both already exist");
        }
        if (phoneExists) {
            throw new DuplicateDataException("Phone number already exists");
        }
        if (locationExists) {
            throw new DuplicateDataException("This hotel already exists at this location");
        }

        return hotelRepository.save(hotel);
    }


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
                            hotel.getPhoneNo()
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

        if(!loggedUser.getRole().equals("HOTEL")){
            throw new UnauthorizedAccessException("Only hotel can perform check-in");
        }

        Booking booking = bookingRepository.findById(request.getBookingId())
                .orElseThrow(()-> new DataNotFoundException("Booking not found."));

        if(!booking.getHotel().getId().equals(hotelId)){
            throw new UnauthorizedAccessException("You cannot check-in bookings of another hotel");
        }

        LocalDate today = LocalDate.now();
        LocalDate checkInDate = booking.getCheckInDate();

        if(today.isBefore(checkInDate)|| today.isAfter(checkInDate) ){
            throw new BookingCheckInException("Check-in is allowed only on check-in date or between booking dates.");
        }
        // verifying user identity
        if(!booking.getUser().getId().equals(request.getUserId())){
            throw new IllegalArgumentException("User mismatch");
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

}
