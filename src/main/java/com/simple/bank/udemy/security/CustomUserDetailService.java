package com.simple.bank.udemy.security;

import com.simple.bank.udemy.auth.entity.UserEntity;
import com.simple.bank.udemy.auth.repository.UserRepository;
import com.simple.bank.udemy.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = userRepository.findByEmail(username).orElseThrow(() -> new NotFoundException("Email not found"));

        return AuthUser.builder()
                .user(user)
                .build();
    }
}
