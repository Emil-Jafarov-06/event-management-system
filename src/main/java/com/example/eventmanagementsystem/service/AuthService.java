package com.example.eventmanagementsystem.service;

import com.example.eventmanagementsystem.exception.ForbiddenAccessException;
import com.example.eventmanagementsystem.model.entity.User;
import com.example.eventmanagementsystem.model.enums.RoleEnum;
import com.example.eventmanagementsystem.model.request.LoginRequest;
import com.example.eventmanagementsystem.model.request.RefreshRequest;
import com.example.eventmanagementsystem.model.request.RegisterRequest;
import com.example.eventmanagementsystem.model.response.AuthResponse;
import com.example.eventmanagementsystem.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final UserDetailsService userDetailsService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthResponse register(RegisterRequest authRequest) {
        if(authRequest.getRoles().contains(RoleEnum.ADMIN)){
            throw new ForbiddenAccessException("Cannot register as admin! Please contact the system administrator for help. (Role: " + RoleEnum.ADMIN + " )");
        }
        User user = new User();
        log.info(authRequest.toString());

        user.setUsername(authRequest.getUsername());
        user.setPassword(passwordEncoder.encode(authRequest.getPassword()));
        authRequest.getRoles().forEach(role -> user.getRoles().add(role));
        user.setEmail(authRequest.getEmail());

        User registeredUser = userService.registerUser(user);

        UserDetails userDetails = userDetailsService.loadUserByUsername(registeredUser.getUsername());

        String accessToken = jwtService.generateAccessToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        return new AuthResponse(accessToken, refreshToken);

    }

    public AuthResponse login(LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        String accessToken = jwtService.generateAccessToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        return new AuthResponse(accessToken, refreshToken);

    }

    public AuthResponse refreshToken(RefreshRequest refreshRequest) {
        String refreshToken = refreshRequest.getRefreshToken();

        String username = jwtService.extractUsername(refreshToken);

        if (username != null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            if (jwtService.isTokenValid(refreshToken, userDetails)) {
                String accessToken = jwtService.generateAccessToken(userDetails);

                return new AuthResponse(accessToken, refreshToken);
            }
        }

        return null;
    }
}
