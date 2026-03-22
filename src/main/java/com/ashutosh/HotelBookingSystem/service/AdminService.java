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
    private final RoomRepository roomRepository;


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

    public AdminHotelDashboardDTO getSpecificHotelDetails(Long hotelId) {

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

        Long totalRooms = roomRepository.countByHotel_Id(hotelId);

        Double totalBookingRevenue = bookingRepository.getTotalBookingRevenue(hotelId);

        AddressDTO addressDTO = new AddressDTO(
                hotel.getAddressLine(),
                hotel.getCity(),
                hotel.getState(),
                hotel.getPinCode()
        );

        AdminHotelFullInfoDTO hotelInfo = new AdminHotelFullInfoDTO(
                hotel.getId(),
                hotel.getHotelName(),
                hotel.getRegisteredOwnerName(),
                hotel.getPhoneNo(),
                hotel.getEmail(),
                hotel.getGstNo(),
                hotel.getGovtRegisteredNo(),
                addressDTO,
                hotel.getStatus(),
                hotel.getCreatedAt()
        );

        Long onlineBookings = bookingRepository.countByHotel_IdAndBookingSource(hotelId,BookingSource.ONLINE);
        Long offlineBookings = bookingRepository.countByHotel_IdAndBookingSource(hotelId,BookingSource.OFFLINE);

        AdminBookingStatsDTO bookingStats = new AdminBookingStatsDTO(
                totalRooms,
                totalBooking,
                confirmedBookings,
                completedBookings,
                cancelledBooking,
                onlineBookings,
                offlineBookings
        );

        AdminFinancialStatsDTO financialStats = new AdminFinancialStatsDTO(
                totalBookingRevenue,
                totalCommission,
                bookingCommission,
                registrationCommission
        );

        return new AdminHotelDashboardDTO(
          hotelInfo,
          bookingStats,
          financialStats
        );
    }

    public PlatformDashboardDTO getPlatformDashboard() {

        Long approvedHotels = hotelRepository.countByStatus(HotelStatus.APPROVED);
        Long pendingHotels = hotelRepository.countByStatus(HotelStatus.PENDING);
        Long blockedHotels = hotelRepository.countByStatus(HotelStatus.BLOCKED);
        Long rejectedHotels = hotelRepository.countByStatus(HotelStatus.BLOCKED);
        Long totalHotels = approvedHotels + pendingHotels + blockedHotels;

        PlatformHotelCountStatsDTO hotelStats = new PlatformHotelCountStatsDTO(
                totalHotels,
                approvedHotels,
                pendingHotels,
                blockedHotels,
                rejectedHotels
        );

        BookingStatsProjection stats = bookingRepository.getBookingStats();

        Long totalBooking = stats.getTotalBookings();
        Long totalConfirmedBookings = stats.getConfirmedBookings();
        Long totalCompletedBookings = stats.getCompletedBookings();
        Long totalCancelledBooking = stats.getCancelledBookings();

        Long onlineBooking = stats.getOnlineBookings();
        Long offlineBooking = stats.getOfflineBookings();

        PlatformBookingStats bookingStats = new PlatformBookingStats(
                totalBooking,
                totalConfirmedBookings,
                totalCompletedBookings,
                totalCancelledBooking,
                onlineBooking,
                offlineBooking
        );

        Double registrationCommission = commissionRepository.getTotalByType(CommissionType.REGISTRATION);
        Double bookingCommission = commissionRepository.getTotalByType(CommissionType.BOOKING);
        Double totalPlatformEarnings = registrationCommission + bookingCommission;

        PlatformRevenueStats revenueStats = new PlatformRevenueStats(
                registrationCommission,
                bookingCommission,
                totalPlatformEarnings
        );

        return new PlatformDashboardDTO(
                "WELCOME to ADMIN PANEL",
                hotelStats,
                bookingStats,
                revenueStats
        );
    }
    
    public AdminPendingHotelResponseDTO getPendingHotels(){
        List<Hotel> hotels = hotelRepository.findByStatus(HotelStatus.PENDING);

        if(hotels.isEmpty()){
            throw new DataNotFoundException("No pending hotels found");
        }

        List<AdminHotelApprovingDTO> hotelList = hotels.stream()
                .map(hotel -> {

                    AddressDTO address = new AddressDTO();
                    address.setAddressLine(hotel.getAddressLine());
                    address.setCity(hotel.getCity());
                    address.setState(hotel.getState());
                    address.setPinCode(hotel.getPinCode());

                    AdminHotelApprovingDTO dto = new AdminHotelApprovingDTO();

                    dto.setHotelId(hotel.getId());
                    dto.setHotelName(hotel.getHotelName());
                    dto.setAddress(address);
                    dto.setCreatedAt(hotel.getCreatedAt());
                    dto.setEmail(hotel.getEmail());
                    dto.setPhoneNo(hotel.getPhoneNo());
                    dto.setOwnerName(hotel.getRegisteredOwnerName());
                    dto.setGstNo(hotel.getGstNo());
                    dto.setGovtRegisteredNo(hotel.getGovtRegisteredNo());
                    dto.setStatus(hotel.getStatus());

                    return dto;
                }).toList();

        return new AdminPendingHotelResponseDTO(
                (long) hotels.size(),
                hotelList
        );
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

        return "Hotel "+ hotel.getHotelName() +" approved successfully";
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

        return "Hotel "+ hotel.getHotelName() +" rejected successfully";
    }

    @Transactional
    public String blockHotel(Long hotelId){
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(()-> new DataNotFoundException("Hotel not found with id: "+ hotelId));

        hotel.setStatus(HotelStatus.BLOCKED);

        return "Hotel "+ hotel.getHotelName() +" approved successfully";
    }

    @Transactional
    public String unblockHotel(Long hotelId){
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(()-> new DataNotFoundException("Hotel not found"));

        if(hotel.getStatus() != HotelStatus.BLOCKED){
            throw new HotelRegisterException("Hotel is not blocked");
        }
        hotel.setStatus(HotelStatus.APPROVED);

        ;return "Hotel "+ hotel.getHotelName() +" approved successfully";
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
