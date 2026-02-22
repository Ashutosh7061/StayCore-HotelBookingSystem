package com.ashutosh.HotelBookingSystem.service;

import com.ashutosh.HotelBookingSystem.Enum.BookingStatus;
import com.ashutosh.HotelBookingSystem.dto.HotelResponseDTO;
import com.ashutosh.HotelBookingSystem.dto.GetAllUserPerHotelResponseDTO;
import com.ashutosh.HotelBookingSystem.entity.Hotel;
import com.ashutosh.HotelBookingSystem.entity.User;
import com.ashutosh.HotelBookingSystem.exception.DataNotFoundException;
import com.ashutosh.HotelBookingSystem.exception.DuplicateDataException;
import com.ashutosh.HotelBookingSystem.repository.BookingRepository;
import com.ashutosh.HotelBookingSystem.repository.HotelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HotelService {

    private final HotelRepository hotelRepository;
    private final BookingRepository bookingRepository;

    public Hotel registerHotel(Hotel hotel){

        boolean phoneExists = hotelRepository.existsByContact(hotel.getContact());

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

    //
    public List<HotelResponseDTO> getAllHotels(){

        List<Hotel> hotels = hotelRepository.findAll();

        return hotels.stream()
                .map(hotel ->new HotelResponseDTO(
                        hotel.getId(),
                        hotel.getHotelName(),
                        hotel.getAddressLine(),
                        hotel.getCity(),
                        hotel.getState(),
                        hotel.getPinCode(),
                        hotel.getContact()
                ))
                .toList();
    }


    public Hotel getHotelWithId(Long hotelId){

        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(()-> new DataNotFoundException("Hotel not found for this id"));

        return hotel;
    }


    public List<GetAllUserPerHotelResponseDTO> getAllUserOfHotelByStatus(Long hotelId, BookingStatus status){

        hotelRepository.findById(hotelId)
                .orElseThrow(() ->
                        new DataNotFoundException("Hotel not found with id: " + hotelId)
                );

        List<User> users = hotelRepository.findUsersByHotelAndStatus(hotelId, status);

        if (users.isEmpty()) {
            throw new DataNotFoundException(
                    "No users found for this hotel with status " + status
            );
        }
        return users.stream()
                .map(user -> new GetAllUserPerHotelResponseDTO(
                        user.getId(),
                        user.getName(),
                        user.getEmail(),
                        user.getPhoneNo(),
                        user.getCreatedAt()
                ))
                .toList();
    }

}
