package com.ashutosh.HotelBookingSystem.service;

import com.ashutosh.HotelBookingSystem.Enum.IdType;
import com.ashutosh.HotelBookingSystem.entity.User;
import com.ashutosh.HotelBookingSystem.exception.DuplicateDataException;
import com.ashutosh.HotelBookingSystem.exception.InvalidIdNumberException;
import com.ashutosh.HotelBookingSystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User registerUser(User user){

        validateGovernmentId(user.getUniqueIdType(), user.getUniqueIdNumber());

        boolean exists = userRepository.existsByUniqueIdNumber(user.getUniqueIdNumber());

        if(exists){
            throw new DuplicateDataException("This uniqueID number is already registered");
        }
        return userRepository.save(user);
    }

    public User loginUser(String email){
        return userRepository.findByEmail(email)
                .orElseThrow(()-> new DuplicateDataException("User not found"));
    }

    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    private void validateGovernmentId(IdType idType, String uniqueIdNumber) {

        if (idType == IdType.AADHAR) {
            if (!uniqueIdNumber.matches("\\d{12}")) {
                throw new InvalidIdNumberException(
                        "Aadhaar must be exactly 12 digits"
                );
            }
        }
        if (idType == IdType.VOTER_CARD) {
            if (!uniqueIdNumber.matches("^[A-Z]{3}[0-9]{7}$")) {
                throw new InvalidIdNumberException(
                        "Voter ID must be 3 uppercase letters followed by 7 digits"
                );
            }
        }
    }


}
