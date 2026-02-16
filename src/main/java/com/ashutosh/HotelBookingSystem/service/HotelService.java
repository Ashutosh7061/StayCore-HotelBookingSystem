package com.ashutosh.HotelBookingSystem.service;

import com.ashutosh.HotelBookingSystem.entity.Hotel;
import com.ashutosh.HotelBookingSystem.exception.DataNotFoundException;
import com.ashutosh.HotelBookingSystem.exception.DuplicateDataException;
import com.ashutosh.HotelBookingSystem.repository.HotelRepository;
import com.ashutosh.HotelBookingSystem.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HotelService {

    private final HotelRepository hotelRepository;
    private final RoomRepository roomRepository;

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

    public List<Hotel> getAllHotels(){
        return hotelRepository.findAll();
    }

    public Hotel getHotelWithId(Long hotelId){

        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(()-> new DataNotFoundException("Hotel not found for this id"));

        return hotel;
    }

}
