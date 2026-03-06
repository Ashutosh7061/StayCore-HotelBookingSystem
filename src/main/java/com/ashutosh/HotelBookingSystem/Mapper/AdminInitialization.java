package com.ashutosh.HotelBookingSystem.Mapper;

import com.ashutosh.HotelBookingSystem.Enum.Role;
import com.ashutosh.HotelBookingSystem.entity.AuthUser;
import com.ashutosh.HotelBookingSystem.repository.AuthUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminInitialization implements CommandLineRunner {

    private final AuthUserRepository  authUserRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args){
        if(authUserRepository.findByEmail("staycore@admin.com").isEmpty()){

            AuthUser admin = new AuthUser();

            admin.setEmail("staycore@admin.com");
            admin.setPassword(passwordEncoder.encode("staycore@123"));
            admin.setRole(Role.ADMIN);
            admin.setReferenceId(null);

            authUserRepository.save(admin);

            System.out.println("Default Admin Created");
        }
    }
}
