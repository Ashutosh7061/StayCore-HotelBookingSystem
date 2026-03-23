package com.ashutosh.HotelBookingSystem.service;

import com.ashutosh.HotelBookingSystem.Enum.HotelStatus;
import com.ashutosh.HotelBookingSystem.Enum.RoomStatus;
import com.ashutosh.HotelBookingSystem.dto.AddressDTO;
import com.ashutosh.HotelBookingSystem.dto.AvailableHotelDTO;
import com.ashutosh.HotelBookingSystem.entity.Hotel;
import com.ashutosh.HotelBookingSystem.entity.Room;
import com.ashutosh.HotelBookingSystem.repository.HotelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final HotelRepository hotelRepository;

    public List<AvailableHotelDTO> searchByCity(String city, Double minPrice, Double maxPrice,String sort,int page, int size){

        Pageable pageable = PageRequest.of(page, size);

        Page<Hotel> hotels = hotelRepository.findByStatusAndCityIgnoreCase(HotelStatus.APPROVED, city, pageable);

        List<AvailableHotelDTO> result = hotels.getContent()
                .stream()
                .map(hotel->{

                    AddressDTO address = new AddressDTO(
                            hotel.getAddressLine(),
                            hotel.getCity(),
                            hotel.getState(),
                            hotel.getPinCode()
                    );

                    List<Room> availableRooms = hotel.getRooms()
                            .stream()
                            .filter(room -> room.getStatus()== RoomStatus.VACANT)
                            .toList();

                    Double hotelMinPrice = availableRooms.stream()
                            .map(Room::getPrice)
                            .min(Double::compare)
                            .orElse(null);

                    boolean priceMatches = true;

                    if(minPrice != null && hotelMinPrice != null && hotelMinPrice < minPrice) {
                        priceMatches = false;
                    }
                    if (maxPrice != null && hotelMinPrice != null && hotelMinPrice > maxPrice) {
                        priceMatches = false;
                    }

                    List<String> roomTypes = availableRooms.stream()
                            .map(Room::getRoomType)
                            .distinct()
                            .toList();

                    return new AvailableHotelDTO(
                            hotel.getId(),
                            hotel.getHotelName(),
                            address,
                            hotelMinPrice,
                            roomTypes
                    );
                }).filter(dto -> dto.getMinPrice() != null)
                  .filter(dto -> {
                    boolean matches = true;

                    if (minPrice != null && dto.getMinPrice() < minPrice) {
                        matches = false;
                    }

                    if (maxPrice != null && dto.getMinPrice() > maxPrice) {
                        matches = false;
                    }

                    return matches;
                })
                .toList();

        if("price_acs".equalsIgnoreCase(sort)){
            result = result.stream()
                    .sorted((h1,h2)-> Double.compare(h1.getMinPrice(),h2.getMinPrice()))
                    .toList();
        }

        return result;
    }
}
