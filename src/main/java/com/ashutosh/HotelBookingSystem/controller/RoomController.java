package com.ashutosh.HotelBookingSystem.controller;

import com.ashutosh.HotelBookingSystem.entity.Room;
import com.ashutosh.HotelBookingSystem.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    @PostMapping("/{hotelId}/addRoom")
    public Room addRoom(@PathVariable Long hotelId,@RequestBody Room room){
        return roomService.addRoom(hotelId,room);
    }

    @GetMapping("/hotel/{hotelId}")
    public List<Room> getRooms(@PathVariable Long hotelId){
        return roomService.getRoomsByHotel(hotelId);
    }
}
