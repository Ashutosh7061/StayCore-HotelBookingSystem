package com.ashutosh.HotelBookingSystem.service;

import com.ashutosh.HotelBookingSystem.entity.User;
import com.ashutosh.HotelBookingSystem.exception.DataNotFoundException;
import com.ashutosh.HotelBookingSystem.exception.DuplicateDataException;
import com.ashutosh.HotelBookingSystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.ashutosh.HotelBookingSystem.Mapper.helperFunctions;


import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User registerUser(User user){

        helperFunctions.validateGovernmentId(user.getUniqueIdType(), user.getUniqueIdNumber());
        helperFunctions.isValidPhoneNumber(user.getPhoneNo());
        boolean exists = userRepository.existsByUniqueIdNumber(user.getUniqueIdNumber());

        if(exists){
            throw new DuplicateDataException("This uniqueID number is already registered");
        }

        boolean phoneNoExists = userRepository.existsByPhoneNo(user.getPhoneNo());
        boolean emailIdExists = userRepository.existsByEmail(user.getEmail());

        if(emailIdExists && phoneNoExists){
            throw new DuplicateDataException("Email and Phone number are already registered.");
        }
        if(phoneNoExists){
            throw new DuplicateDataException("This phone number is already registered.");
        }
        if(emailIdExists){
            throw new DuplicateDataException("This email id is already registered.");
        }
        return userRepository.save(user);
    }


    public User loginUser(String email){
        return userRepository.findByEmail(email)
                .orElseThrow(()-> new DataNotFoundException("User not found for this email Id."));
    }


    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

}
