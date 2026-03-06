package com.ashutosh.HotelBookingSystem.service;

import com.ashutosh.HotelBookingSystem.Enum.Role;
import com.ashutosh.HotelBookingSystem.Mapper.helperFunctions;
import com.ashutosh.HotelBookingSystem.dto.HotelSignupRequestDTO;
import com.ashutosh.HotelBookingSystem.dto.LoginRequestDTO;
import com.ashutosh.HotelBookingSystem.dto.LoginResponseDTO;
import com.ashutosh.HotelBookingSystem.dto.UserSignupRequestDTO;
import com.ashutosh.HotelBookingSystem.entity.AuthUser;
import com.ashutosh.HotelBookingSystem.entity.Hotel;
import com.ashutosh.HotelBookingSystem.entity.User;
import com.ashutosh.HotelBookingSystem.exception.DuplicateDataException;
import com.ashutosh.HotelBookingSystem.exception.InvalidIdNumberException;
import com.ashutosh.HotelBookingSystem.repository.AuthUserRepository;
import com.ashutosh.HotelBookingSystem.repository.HotelRepository;
import com.ashutosh.HotelBookingSystem.repository.UserRepository;
import com.ashutosh.HotelBookingSystem.security.CustomUserDetails;
import com.ashutosh.HotelBookingSystem.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthUserRepository authUserRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final HotelRepository hotelRepository;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;


    public String registerUser(UserSignupRequestDTO request){

        if(authUserRepository.findByEmail(request.getEmail()).isPresent()){
            throw new DuplicateDataException("Email already registered");
        }

        helperFunctions.validateGovernmentId(request.getUniqueIdType(), request.getUniqueIdNumber());
        helperFunctions.isValidPhoneNumber(request.getPhoneNo());

        boolean exists = userRepository.existsByUniqueIdNumber(request.getUniqueIdNumber());

        if(exists){
            throw new DuplicateDataException("This uniqueID number is already registered");
        }

        boolean phoneNoExists = userRepository.existsByPhoneNo(request.getPhoneNo());
        boolean emailIdExists = userRepository.existsByEmail(request.getEmail());

        if(emailIdExists && phoneNoExists){
            throw new DuplicateDataException("Email and Phone number are already registered.");
        }
        if(phoneNoExists){
            throw new DuplicateDataException("This phone number is already registered.");
        }
        if(emailIdExists){
            throw new DuplicateDataException("This email id is already registered.");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPhoneNo(request.getPhoneNo());
        user.setAddress(request.getAddress());
        user.setUniqueIdType(request.getUniqueIdType());
        user.setUniqueIdNumber(request.getUniqueIdNumber());

        User savedUser = userRepository.save(user);

        AuthUser authUser = new AuthUser();
        authUser.setEmail(request.getEmail());
        authUser.setPassword(passwordEncoder.encode(request.getPassword()));
        authUser.setRole(Role.USER);
        authUser.setReferenceId(savedUser.getId());

        authUserRepository.save(authUser);

        return " Welcome😊 " + request.getName()+ " \n You have successfully registered.";
    }

    public String registerHotel(HotelSignupRequestDTO request) {

        if (authUserRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new DuplicateDataException("Email already registered");
        }

        helperFunctions.isValidPhoneNumber(request.getPhoneNo());

        if(hotelRepository.existsByGovtRegisteredNo(request.getGovtRegisteredNo())){
            throw new InvalidIdNumberException("Hotel already exists with the same Govt Registered Number");
        }

        boolean emailIdExists = hotelRepository.existsByEmail(request.getEmail());

        boolean phoneExists = hotelRepository.existsByPhoneNo(request.getPhoneNo());

        boolean locationExists = hotelRepository.existsByHotelNameAndAddressLineAndCityAndPinCode(
                request.getHotelName(),
                request.getAddressLine(),
                request.getCity(),
                request.getPinCode());

        if(phoneExists && locationExists && emailIdExists){
            throw new DuplicateDataException("Multiple duplicate data found(email, phoneNo, and address)");
        }

        if(phoneExists && locationExists){
            throw new DuplicateDataException("Phone number and hotel location both already exist");
        }
        if (phoneExists) {
            throw new DuplicateDataException("Phone number already exists");
        }
        if (locationExists) {
            throw new DuplicateDataException("This hotel already exists at this location");
        }

        Hotel hotel = new Hotel();
        hotel.setHotelName(request.getHotelName());
        hotel.setAddressLine(request.getAddressLine());
        hotel.setCity(request.getCity());
        hotel.setState(request.getState());
        hotel.setPinCode(request.getPinCode());
        hotel.setPhoneNo(request.getPhoneNo());
        hotel.setEmail(request.getEmail());
        hotel.setGstNo(request.getGstNo());
        hotel.setRegisteredOwnerName(request.getRegisteredOwnerName());
        hotel.setGovtRegisteredNo(request.getGovtRegisteredNo());

        Hotel savedHotel = hotelRepository.save(hotel);

        AuthUser authUser = new AuthUser();
        authUser.setEmail(request.getEmail());
        authUser.setPassword(passwordEncoder.encode(request.getPassword()));
        authUser.setRole(Role.HOTEL);
        authUser.setReferenceId(savedHotel.getId());

        authUserRepository.save(authUser);

        return "Welcome😊 " + request.getRegisteredOwnerName() + "\n Your hotel "+request.getHotelName()+ " is successfully registered. \n We are always open for you.";
    }

    public LoginResponseDTO login(LoginRequestDTO request) {

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.getEmail(),
                                request.getPassword()
                        )
                );

        CustomUserDetails userDetails =
                (CustomUserDetails) authentication.getPrincipal();

        String token = jwtUtil.generateAccessToken(userDetails);

        return new LoginResponseDTO(userDetails.getReferenceId(),userDetails.getRole(),token);
    }

}
