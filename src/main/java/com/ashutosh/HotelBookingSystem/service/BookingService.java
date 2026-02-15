package com.ashutosh.HotelBookingSystem.service;

import com.ashutosh.HotelBookingSystem.Enum.BookingStatus;
import com.ashutosh.HotelBookingSystem.Enum.RoomStatus;
import com.ashutosh.HotelBookingSystem.dto.BookingResponseDTO;
import com.ashutosh.HotelBookingSystem.dto.BookingSummaryDTO;
import com.ashutosh.HotelBookingSystem.dto.HotelBookingResponseDTO;
import com.ashutosh.HotelBookingSystem.dto.UserBookingResponseDTO;
import com.ashutosh.HotelBookingSystem.entity.Booking;
import com.ashutosh.HotelBookingSystem.entity.Hotel;
import com.ashutosh.HotelBookingSystem.entity.Room;
import com.ashutosh.HotelBookingSystem.entity.User;
import com.ashutosh.HotelBookingSystem.repository.BookingRepository;
import com.ashutosh.HotelBookingSystem.repository.HotelRepository;
import com.ashutosh.HotelBookingSystem.repository.RoomRepository;
import com.ashutosh.HotelBookingSystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
    public BookingResponseDTO bookRoom(Long userId, Long hotelId, String roomType, int noOfDays, int noOfRooms){
        User user = userRepository.findById(userId).
                orElseThrow(()-> new RuntimeException("User not found."));

        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(()-> new RuntimeException("Hotel not found"));

        List<Room> availableRooms = roomRepository
                .findByHotel_IdAndRoomTypeAndStatus(hotelId, roomType, RoomStatus.VACENT);

        if(availableRooms.size() < noOfRooms){
            throw new RuntimeException("Not enough rooms available, there are only "+ availableRooms.size()+" rooms available");
        }

        //Price calculation
        double pricePerRoom = availableRooms.get(0).getPrice();
        double totalPrice = pricePerRoom * noOfDays * noOfRooms;

        //Updating rooms status
        List<String> allottedRooms = new ArrayList<>();

        for(int i = 0 ; i < noOfRooms ; i++){
            Room room = availableRooms.get(i);
            room.setStatus(RoomStatus.BOOKED);
            allottedRooms.add(room.getRoomNumber());
        }

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setHotel(hotel);
        booking.setRoomType(roomType);
        booking.setNumberOfDays(noOfDays);
        booking.setNumberOfRooms(noOfRooms);
        booking.setTotalPrice(totalPrice);
        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setBookingTime(LocalDateTime.now());
        booking.setAllottedRoomNumber(allottedRooms);

//        return bookingRepository.save(booking);
        Booking savedBooking = bookingRepository.save(booking);

        return new BookingResponseDTO(
                savedBooking.getId(),
                user.getId(),
                user.getName(),
                hotel.getId(),
                hotel.getHotelName(),
                allottedRooms,
                noOfDays,
                noOfRooms,
                totalPrice,
                savedBooking.getStatus().name(),
                savedBooking.getBookingTime()
        );

    }

    public List<UserBookingResponseDTO> getUserBookings(Long userId){

        List<Booking> bookings = bookingRepository.findByUserId(userId);

        return bookings.stream()
                .map(booking -> new UserBookingResponseDTO(
                        booking.getId(),
                        booking.getHotel().getHotelName(),
                        booking.getHotel().getAddressLine(),
                        booking.getAllottedRoomNumber(),
                        booking.getNumberOfDays(),
                        booking.getNumberOfRooms(),
                        booking.getTotalPrice(),
                        booking.getStatus().name(),
                        booking.getBookingTime()
                ))
                .toList();
    }

    public List<HotelBookingResponseDTO> getHotelBookings(Long hotelId){

        List<Booking> bookings = bookingRepository.findByHotelId(hotelId);

        return bookings.stream()
                .map(booking -> new HotelBookingResponseDTO(
                        booking.getId(),
                        booking.getUser().getName(),
                        booking.getAllottedRoomNumber(),
                        booking.getNumberOfDays(),
                        booking.getNumberOfRooms(),
                        booking.getTotalPrice(),
                        booking.getStatus().name(),
                        booking.getBookingTime()
                ))
                .toList();
    }

    public BookingSummaryDTO getBookingSummary(Long bookingId){

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(()-> new RuntimeException("Booking not found"));

        return new BookingSummaryDTO(
                booking.getId(),
                booking.getUser().getId(),
                booking.getUser().getName(),
                booking.getUser().getPhoneNo(),

                booking.getHotel().getId(),
                booking.getHotel().getHotelName(),
                booking.getHotel().getAddressLine(),

                booking.getAllottedRoomNumber(),

                booking.getNumberOfDays(),
                booking.getNumberOfRooms(),

                booking.getTotalPrice(),

                booking.getStatus().name(),

                booking.getBookingTime()
        );
    }
}
