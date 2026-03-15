package com.ashutosh.HotelBookingSystem.service;

import com.ashutosh.HotelBookingSystem.Enum.Role;
import com.ashutosh.HotelBookingSystem.Enum.SupportStatus;
import com.ashutosh.HotelBookingSystem.dto.SupportRequestDTO;
import com.ashutosh.HotelBookingSystem.dto.SupportTicketResponseDTO;
import com.ashutosh.HotelBookingSystem.entity.Hotel;
import com.ashutosh.HotelBookingSystem.entity.SupportMessage;
import com.ashutosh.HotelBookingSystem.entity.SupportRequest;
import com.ashutosh.HotelBookingSystem.entity.User;
import com.ashutosh.HotelBookingSystem.exception.DataNotFoundException;
import com.ashutosh.HotelBookingSystem.exception.SupportRequestException;
import com.ashutosh.HotelBookingSystem.exception.UnauthorizedAccessException;
import com.ashutosh.HotelBookingSystem.repository.HotelRepository;
import com.ashutosh.HotelBookingSystem.repository.SupportMessageRepository;
import com.ashutosh.HotelBookingSystem.repository.SupportRequestRepository;
import com.ashutosh.HotelBookingSystem.repository.UserRepository;
import com.ashutosh.HotelBookingSystem.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SupportService {

    private final SupportRequestRepository supportRequestRepository;
    private final UserRepository userRepository;
    private final HotelRepository hotelRepository;
    private final SupportMessageRepository supportMessageRepository;

    public String createSupportRequest(SupportRequestDTO request){

        CustomUserDetails loggedUser = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Long referenceID = loggedUser.getReferenceId();

        Role role = Role.valueOf(loggedUser.getRole());

        SupportRequest supportRequest = new SupportRequest();

        supportRequest.setTokenId(generateTokenId());
        supportRequest.setReferenceId(referenceID);
        supportRequest.setRole(role);
        supportRequest.setMessage(request.getMessage());

        if(role.equals(Role.USER)){
            User user = userRepository.findById(referenceID).orElseThrow();
            supportRequest.setRequesterName(user.getName());
            supportRequest.setRequesterEmail(user.getEmail());
        }
        else if(role.equals(Role.HOTEL)){

            Hotel hotel = hotelRepository.findById(referenceID).orElseThrow();
            supportRequest.setRequesterName(hotel.getHotelName());
            supportRequest.setRequesterEmail(hotel.getEmail());
        }

        supportRequestRepository.save(supportRequest);

        SupportMessage supportMessage= new SupportMessage();

        supportMessage.setSupportRequest(supportRequest);
        supportMessage.setSenderRole(role);
        supportMessage.setMessage(request.getMessage());

        supportMessageRepository.save(supportMessage);

        return "Your Query was submitted successfully.\nToken ID: " + supportRequest.getTokenId();
    }

//    private String generateTokenId(){
//        return "TKT-" + System.currentTimeMillis();
//    }

    public SupportTicketResponseDTO getTicketByToken(String  tokenId){

        CustomUserDetails loggedUser = (CustomUserDetails)SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        Long loggedUserId = loggedUser.getReferenceId();
        Role loggedRole = Role.valueOf(loggedUser.getRole());

        SupportRequest ticket = supportRequestRepository.findByTokenId(tokenId)
                .orElseThrow(()-> new DataNotFoundException("Ticket not found"));

        // role validations
        if(!ticket.getReferenceId().equals(loggedUserId) || !ticket.getRole().equals(loggedRole)){
            throw new UnauthorizedAccessException("You are not authorized to view this token");
        }

        List<SupportMessage> messages = supportMessageRepository.findBySupportRequestIdOrderByCreatedAtAsc(ticket.getId());

        SupportTicketResponseDTO response = new SupportTicketResponseDTO();

        response.setTokenId(ticket.getTokenId());
        response.setStatus(ticket.getStatus());
        response.setCreatedAt(ticket.getCreatedAt());

        List<SupportTicketResponseDTO.MessageDTO> messageList = messages.stream()
                .map(msg -> {
            SupportTicketResponseDTO.MessageDTO dto = new SupportTicketResponseDTO.MessageDTO();
            dto.setSenderRole(msg.getSenderRole());
            dto.setMessage(msg.getMessage());
            dto.setCreatedAt(msg.getCreatedAt());
            return dto;
        }).toList();

        response.setMessage(messageList);

        return response;
    }

    public String replyToTicketByUserOrHotel(String tokenId, String message){

        CustomUserDetails loggedUser = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();


        Long loggedUserId = loggedUser.getReferenceId();
        Role role = Role.valueOf(loggedUser.getRole());

        SupportRequest ticket = supportRequestRepository.findByTokenId(tokenId)
                .orElseThrow(()-> new DataNotFoundException("Ticket not found"));

        if(ticket.getStatus() == SupportStatus.valueOf("RESOLVED")){
            throw new SupportRequestException("Issue has been already resolved");
        }

        if(!ticket.getReferenceId().equals(loggedUserId) || !ticket.getRole().equals(role)){
            throw new UnauthorizedAccessException("YOu are not authorized to view this ticket");
        }

        SupportMessage message1 = new SupportMessage();

        message1.setSupportRequest(ticket);
        message1.setSenderRole(role);
        message1.setMessage(message);

        supportMessageRepository.save(message1);

        return "Reply sent successfully";
    }

    private  String generateTokenId(){

        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        SecureRandom random = new SecureRandom();
        String token;

        do{
            StringBuilder builder = new StringBuilder("TKT-");

            for(int i = 0; i < 6 ; i++){
                int index = random.nextInt(characters.length());
                builder.append(characters.charAt(index));
            }
            token = builder.toString();
        }while (supportRequestRepository.findByTokenId(token).isPresent());

        return token;
    }
}
