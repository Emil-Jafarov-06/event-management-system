package com.example.eventmanagementsystem.security;

import com.example.eventmanagementsystem.repository.UserRepository;
import com.example.eventmanagementsystem.security.SecurityUser;
import com.example.eventmanagementsystem.model.entity.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AuthUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(EntityNotFoundException::new);
        return new SecurityUser(user);
    }
}

