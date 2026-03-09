package com.ashutosh.HotelBookingSystem.service;

import com.ashutosh.HotelBookingSystem.Enum.Role;
import com.ashutosh.HotelBookingSystem.dto.SupportRequestDTO;
import com.ashutosh.HotelBookingSystem.entity.Hotel;
import com.ashutosh.HotelBookingSystem.entity.SupportRequest;
import com.ashutosh.HotelBookingSystem.entity.User;
import com.ashutosh.HotelBookingSystem.repository.HotelRepository;
import com.ashutosh.HotelBookingSystem.repository.SupportRequestRepository;
import com.ashutosh.HotelBookingSystem.repository.UserRepository;
import com.ashutosh.HotelBookingSystem.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SupportService {

    private final SupportRequestRepository supportRequestRepository;
    private final UserRepository userRepository;
    private final HotelRepository hotelRepository;

    public String createSupportRequest(SupportRequestDTO request){

        CustomUserDetails loggedUser = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Long referenceID = loggedUser.getReferenceId();

        Role role = Role.valueOf(loggedUser.getRole());

        SupportRequest supportRequest = new SupportRequest();

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

        return "Your Query was submitted to the admin";
    }
}
