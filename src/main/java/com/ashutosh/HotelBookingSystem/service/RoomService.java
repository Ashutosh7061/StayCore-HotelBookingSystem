package com.ashutosh.HotelBookingSystem.service;

import com.ashutosh.HotelBookingSystem.dto.AddRoomRequestDTO;
import com.ashutosh.HotelBookingSystem.dto.AddRoomResponseDTO;
import com.ashutosh.HotelBookingSystem.dto.AdminRoomDTO;
import com.ashutosh.HotelBookingSystem.entity.Hotel;
import com.ashutosh.HotelBookingSystem.entity.Room;
import com.ashutosh.HotelBookingSystem.exception.DataNotFoundException;
import com.ashutosh.HotelBookingSystem.exception.DuplicateDataException;
import com.ashutosh.HotelBookingSystem.repository.HotelRepository;
import com.ashutosh.HotelBookingSystem.repository.RoomRepository;
import com.ashutosh.HotelBookingSystem.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final HotelRepository hotelRepository;
    private final CommissionService commissionService;
    private final HotelService hotelService;

    public AddRoomResponseDTO addRoom(AddRoomRequestDTO request){

        CustomUserDetails loggedUser = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long hotelId = loggedUser.getReferenceId();

        Hotel hotel = hotelRepository.findById(hotelId)
               .orElseThrow(()-> new DataNotFoundException("Hotel not found"));


        hotelService.validateHotelOperation(hotel);

        if(roomRepository.findByHotel_IdAndRoomNumber(hotelId,request.getRoomNumber()).isPresent()){
            throw new DuplicateDataException("Room number already exists in this hotel");
        }

        Room room = new Room();
        room.setRoomNumber(request.getRoomNumber());
        room.setRoomType(request.getRoomType());
        room.setPrice(request.getPrice());
        room.setStatus(request.getStatus());
        room.setHotel(hotel);

        Room savedRoom = roomRepository.save(room);

        commissionService.addRoomRegistrationCommissionFee(hotel);

        return new AddRoomResponseDTO(
                savedRoom.getId(),
                savedRoom.getRoomNumber(),
                savedRoom.getRoomType().toString(),
                savedRoom.getPrice(),
                savedRoom.getStatus()
        );
    }

    public List<AdminRoomDTO> getRoomsByHotel(Long hotelId){
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(()-> new DataNotFoundException("Hotel not found for this id."));

        List<Room> rooms = roomRepository.findByHotel_Id(hotelId);

        return rooms.stream().
                map(room ->new AdminRoomDTO(
                        room.getId(),
                        room.getRoomNumber(),
                        room.getRoomType(),
                        room.getPrice(),
                        room.getStatus().name()
                ))
                .toList();

    }
}
