package com.ashutosh.HotelBookingSystem.repository;

import com.ashutosh.HotelBookingSystem.Enum.RoomStatus;
import com.ashutosh.HotelBookingSystem.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, Long> {
    List<Room> findByHotel_Id(Long hotelId);

    Optional<Room> findByHotel_IdAndRoomNumber(Long hotelId, String roomNumber);

    List<Room> findByHotel_IdAndRoomTypeAndStatus(Long hotelId, String roomType, RoomStatus roomStatus);
}
