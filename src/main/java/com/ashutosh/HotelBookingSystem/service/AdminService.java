package com.ashutosh.HotelBookingSystem.service;

import com.ashutosh.HotelBookingSystem.Enum.*;
import com.ashutosh.HotelBookingSystem.dto.*;
import com.ashutosh.HotelBookingSystem.entity.Hotel;
import com.ashutosh.HotelBookingSystem.entity.SupportMessage;
import com.ashutosh.HotelBookingSystem.entity.SupportRequest;
import com.ashutosh.HotelBookingSystem.entity.User;
import com.ashutosh.HotelBookingSystem.exception.DataNotFoundException;
import com.ashutosh.HotelBookingSystem.exception.HotelRegisterException;
import com.ashutosh.HotelBookingSystem.exception.InvalidBookingStateException;
import com.ashutosh.HotelBookingSystem.exception.MissingValueException;
import com.ashutosh.HotelBookingSystem.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminService {

    private final SupportRequestRepository supportRequestRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final CommissionRepository commissionRepository;
    private final HotelRepository hotelRepository;
    private final SupportMessageRepository supportMessageRepository;

    public List<AdminUserDetailsDTO> getAllUsersForAdmin(){

        List<User> users = userRepository.findAll();

        return users.stream()
                .map(user->{

                    Double totalSpend = bookingRepository.getTotalSpendByUserId(user.getId());

                    Long totalBookings = bookingRepository.getTotalBookingByUserId(user.getId());

                    return new AdminUserDetailsDTO(
                            user.getId(),
                            user.getName(),
                            user.getEmail(),
                            user.getPhoneNo(),
                            user.getCreatedAt(),
                            totalSpend,
                            totalBookings
                    );
                })
                .toList();
    }

    public AdminHotelDetailsDTO getSpecificHotelDetails(Long hotelId) {

        Long totalBooking = (Long)bookingRepository.countByHotelId(hotelId);

        long completedBookings = bookingRepository.countByHotelIdAndStatus(hotelId,BookingStatus.COMPLETED);
        long cancelledBooking = bookingRepository.countByHotelIdAndStatus(hotelId, BookingStatus.CANCELLED);
        long confirmedBookings  = bookingRepository.countByHotelIdAndStatus(hotelId, BookingStatus.CONFIRMED);

        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new DataNotFoundException("Hotel not found"));

        Double totalCommission =
                commissionRepository.getTotalCommissionByHotelId(hotelId);

        Double bookingCommission = commissionRepository.getCommissionByHotelIdAndType(
                        hotelId, CommissionType.BOOKING);

        Double registrationCommission =
                commissionRepository.getCommissionByHotelIdAndType(
                        hotelId, CommissionType.REGISTRATION);

        AddressDTO addressDTO = new AddressDTO(
                hotel.getAddressLine(),
                hotel.getCity(),
                hotel.getState(),
                hotel.getPinCode()
        );

        return new AdminHotelDetailsDTO(
                hotel.getId(),
                hotel.getHotelName(),
                hotel.getAddressLine(),
                hotel.getEmail(),
                addressDTO,
                hotel.getStatus(),
                totalBooking,
                completedBookings,
                cancelledBooking,
                confirmedBookings,
                totalCommission,
                bookingCommission,
                registrationCommission
        );
    }

    public PlatformDashboardDTO getPlatformDashboard() {

        Long totalCompletedBookings =
                bookingRepository.countByStatus(BookingStatus.COMPLETED);

        Double registrationCommission =
                commissionRepository.getTotalByType(CommissionType.REGISTRATION);

        Double bookingCommission =
                commissionRepository.getTotalByType(CommissionType.BOOKING);

        Double totalPlatformEarnings =
                registrationCommission + bookingCommission;

        return new PlatformDashboardDTO(
                "WELCOME to ADMIN PANEL",
                totalCompletedBookings,
                registrationCommission,
                bookingCommission,
                totalPlatformEarnings
        );
    }


    public List<Hotel> getPendingHotels(){
        List<Hotel> hotels = hotelRepository.findByStatus(HotelStatus.PENDING);

        if(hotels.isEmpty()){
            throw new DataNotFoundException("No pending hotels found");
        }
        return hotels;
    }

    @Transactional
    public String approveHotel(Long hotelId){

        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(()-> new DataNotFoundException("Hotel not found with id: "+hotelId));

        if(hotel.getStatus() == (HotelStatus.APPROVED)){
            throw new InvalidBookingStateException("Hotel already approved");
        }

        hotel.setStatus(HotelStatus.APPROVED);
        hotel.setRejectionReason(null);

        return "Hotel approved successfully";
    }


    @Transactional
    public String rejectHotel(Long hotelId, String reason){
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new DataNotFoundException("Hotel not found with id: " + hotelId));

        if(reason == null || reason.isBlank()){
            throw new MissingValueException("Rejection reason required");
        }

        hotel.setStatus(HotelStatus.REJECTED);
        hotel.setRejectionReason(reason);

        return "Hotel rejected successfully";
    }

    @Transactional
    public String blockHotel(Long hotelId){
        Hotel hotel = hotelRepository.findById(hotelId).orElseThrow(()-> new DataNotFoundException("Hotel not found with id: "+ hotelId));

        hotel.setStatus(HotelStatus.BLOCKED);

        return "Hotel blocked Successfully";
    }

    @Transactional
    public String unblockHotel(Long hotelId){

        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(()-> new DataNotFoundException("Hotel not found"));

        if(hotel.getStatus() != HotelStatus.BLOCKED){
            throw new HotelRegisterException("Hotel is not blocked");
        }

        hotel.setStatus(HotelStatus.APPROVED);

        return "Hotel unblocked successfully";
    }

    public List<SupportRequest> getOpenSupportRequests(){
        return  supportRequestRepository.findByStatus(SupportStatus.OPEN);
    }

    @Transactional
    public String resolveSupportRequest(String tokenId){
        SupportRequest request = supportRequestRepository.findByTokenId(tokenId)
                .orElseThrow(()-> new DataNotFoundException("Support request not found"));

        request.setStatus(SupportStatus.RESOLVED);

        return "Support request resolved successfully";
    }

    public String replyToSupportRequest(String tokenId, String message){

        SupportRequest supportRequest = supportRequestRepository.findByTokenId(tokenId)
                .orElseThrow(()-> new DataNotFoundException("Support request not found"));

        SupportMessage supportMessage = new SupportMessage();

        supportMessage.setSupportRequest(supportRequest);
        supportMessage.setSenderRole(Role.ADMIN);
        supportMessage.setMessage(message);

        supportMessageRepository.save(supportMessage);

        supportRequest.setStatus(SupportStatus.IN_PROGRESS);
        supportRequestRepository.save(supportRequest);

        return "Reply sent successfully";

    }

    public SupportTicketResponseDTO getTicketByTokenAdmin(String tokenId){

        SupportRequest ticket = supportRequestRepository.findByTokenId(tokenId)
                .orElseThrow(() -> new DataNotFoundException("Ticket not found"));

        List<SupportMessage> messages =
                supportMessageRepository.findBySupportRequestIdOrderByCreatedAtAsc(ticket.getId());

        SupportTicketResponseDTO response = new SupportTicketResponseDTO();

        response.setTokenId(ticket.getTokenId());
        response.setStatus(ticket.getStatus());
        response.setCreatedAt(ticket.getCreatedAt());

        List<SupportTicketResponseDTO.MessageDTO> messageList =
                messages.stream().map(msg -> {
                    SupportTicketResponseDTO.MessageDTO dto =
                            new SupportTicketResponseDTO.MessageDTO();
                    dto.setSenderRole(msg.getSenderRole());
                    dto.setMessage(msg.getMessage());
                    dto.setCreatedAt(msg.getCreatedAt());
                    return dto;
                }).toList();

        response.setMessage(messageList);

        return response;
    }

}
