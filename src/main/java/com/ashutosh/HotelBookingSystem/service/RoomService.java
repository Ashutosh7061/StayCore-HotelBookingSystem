package com.ashutosh.HotelBookingSystem.service;

import com.ashutosh.HotelBookingSystem.Enum.RoomStatus;
import com.ashutosh.HotelBookingSystem.dto.AddRoomRequestDTO;
import com.ashutosh.HotelBookingSystem.dto.RoomResponseDTO;
import com.ashutosh.HotelBookingSystem.dto.AdminRoomDTO;
import com.ashutosh.HotelBookingSystem.dto.RoomUpdateDTO;
import com.ashutosh.HotelBookingSystem.entity.Hotel;
import com.ashutosh.HotelBookingSystem.entity.Room;
import com.ashutosh.HotelBookingSystem.exception.DataNotFoundException;
import com.ashutosh.HotelBookingSystem.exception.DuplicateDataException;
import com.ashutosh.HotelBookingSystem.exception.RoomNotModifiableException;
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

    public RoomResponseDTO addRoom(AddRoomRequestDTO request){

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

        return new RoomResponseDTO(
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

        List<Room> rooms = roomRepository.findByHotel_IdAndStatus(hotelId, RoomStatus.VACANT);

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

    public RoomResponseDTO updateRoom(String roomNo, RoomUpdateDTO request){

        CustomUserDetails loggedUser = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                             
        Long hotelId = loggedUser.getReferenceId();

        Room room = roomRepository.findByHotel_IdAndRoomNumber(hotelId, roomNo)
                .orElseThrow(()-> new DataNotFoundException("Room not found"));

        if(room.getStatus() != RoomStatus.VACANT){
            throw new RoomNotModifiableException("You can not modify the room, until it is vacant");
        }

        if(request.getRoomType() != null){
            room.setRoomType(request.getRoomType());
        }
        if(request.getPrice() != null){
            room.setPrice(request.getPrice());
        }

        roomRepository.save(room);

        return new RoomResponseDTO(
                room.getId(),
                room.getRoomNumber(),
                room.getRoomType(),
                room.getPrice(),
                room.getStatus()
        );
    }

    public String deleteRoom(String roomNo){

        CustomUserDetails loggedUser = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Long hotelId = loggedUser.getReferenceId();

        Room room = roomRepository.findByHotel_IdAndRoomNumber(hotelId, roomNo)
                .orElseThrow(()-> new DataNotFoundException("Room not found"));

        if(room.getStatus() != RoomStatus.VACANT){
            throw new RoomNotModifiableException("You can not modify the room, until it is vacant");
        }
        roomRepository.delete(room);

        return "Room deleted successfully";
    }

    public List<AdminRoomDTO> getAvailableRooms(String roomType){

        CustomUserDetails loggedUser = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Long hotelId = loggedUser.getReferenceId();

        List<Room> rooms = roomRepository.findByHotel_IdAndRoomTypeAndStatus(hotelId,roomType,RoomStatus.VACANT);

        if(rooms.isEmpty()){
            throw new DataNotFoundException("No rooms available for type: "+ roomType);
        }

        return rooms.stream()
                .map(room -> new AdminRoomDTO(
                        room.getId(),
                        room.getRoomNumber(),
                        room.getRoomType(),
                        room.getPrice(),
                        room.getStatus().name()
                )).toList();
    }

}
