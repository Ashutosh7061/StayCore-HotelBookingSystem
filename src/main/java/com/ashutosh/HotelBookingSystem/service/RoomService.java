package com.ashutosh.HotelBookingSystem.service;

import com.ashutosh.HotelBookingSystem.entity.Hotel;
import com.ashutosh.HotelBookingSystem.entity.Room;
import com.ashutosh.HotelBookingSystem.exception.DataNotFoundException;
import com.ashutosh.HotelBookingSystem.exception.DuplicateDataException;
import com.ashutosh.HotelBookingSystem.repository.HotelRepository;
import com.ashutosh.HotelBookingSystem.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final HotelRepository hotelRepository;
    private final CommissionService commissionService;

    public Room addRoom(Long hotelId,Room room){

       Hotel hotel = hotelRepository.findById(hotelId)
               .orElseThrow(()-> new DataNotFoundException("Hotel not found"));


        if(roomRepository.findByHotel_IdAndRoomNumber(hotelId,room.getRoomNumber()).isPresent()){
            throw new DuplicateDataException("Room number already exists in this hotel");
        }

        room.setHotel(hotel);

        Room savedRoom = roomRepository.save(room);

        commissionService.addRoomRegistrationCommissionFee(hotel);

        return roomRepository.save(room);
    }

    public List<Room> getRoomsByHotel(Long hotelId){
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(()-> new DataNotFoundException("Hotel not found for this id."));

        return roomRepository.findByHotel_Id(hotelId);
    }
}
