package com.ashutosh.HotelBookingSystem.controller;

import com.ashutosh.HotelBookingSystem.dto.*;
import com.ashutosh.HotelBookingSystem.entity.Hotel;
import com.ashutosh.HotelBookingSystem.entity.User;
import com.ashutosh.HotelBookingSystem.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/public")
@RequiredArgsConstructor
public class PublicController {

    private final HotelService hotelService;
    private final RoomService roomService;
    private final AuthService authService;
    private final SearchService searchService;


     @PostMapping("/registerUser")
     public String registerUser(@RequestBody UserSignupRequestDTO request){
        return authService.registerUser(request);
     }

    @PostMapping("/registerHotel")
    public String registerHotel(@RequestBody HotelSignupRequestDTO hotel){
        return authService.registerHotel(hotel);
    }

    @PostMapping("/login")
    public LoginResponseDTO loginUser(@RequestBody LoginRequestDTO request){
        return authService.login(request);
    }

    @GetMapping("/hotel/{hotelId}/allRooms")
    public List<AdminRoomDTO> getRooms(@PathVariable Long hotelId){
        return roomService.getRoomsByHotel(hotelId);
    }

    @GetMapping("/hotels/available")
    public List<AvailableHotelDTO> getHotels(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size){
        return hotelService.getAllAvailableHotels(page, size);
    }

    @GetMapping("/search")
    public List<AvailableHotelDTO> searchHotel(@RequestParam String city,
                                               @RequestParam(required = false) Double minPrice,
                                               @RequestParam(required = false) Double maxPrice,
                                               @RequestParam(required = false) String sort,
                                               @RequestParam(defaultValue = "0")int page,
                                               @RequestParam(defaultValue = "10")int size){
         return searchService.searchByCity(city,minPrice, maxPrice,sort,page,size);
    }



}
