package com.ashutosh.HotelBookingSystem.repository;

import com.ashutosh.HotelBookingSystem.Enum.RoomStatus;
import com.ashutosh.HotelBookingSystem.entity.Room;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, Long> {
    List<Room> findByHotel_Id(Long hotelId);

    Optional<Room> findByHotel_IdAndRoomNumber(Long hotelId, String roomNumber);

    List<Room> findByHotel_IdAndRoomTypeAndStatus(Long hotelId, String roomType, RoomStatus roomStatus);

    Long countByHotel_Id(Long hotelId);

    List<Room> findByHotel_IdAndRoomNumberIn(Long hotelId, List<String> roomNumbers);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT r FROM Room r WHERE r.hotel.id = :hotelId AND r.roomType = :roomType AND r.status = :status")
    List<Room> findAvailableRoomsForUpdate(Long hotelId, String roomType, RoomStatus status);

    long countByHotel_IdAndRoomType(Long hotelId, String roomType);
}
