package com.ashutosh.HotelBookingSystem.security;

import com.ashutosh.HotelBookingSystem.entity.AuthUser;
import com.ashutosh.HotelBookingSystem.repository.AuthUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final AuthUserRepository authUserRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        AuthUser authUser = authUserRepository.findByEmail(email)
                .orElseThrow(()-> new UsernameNotFoundException("User not found."));

        return new CustomUserDetails(authUser);
    }
}
