package com.example.noteapplication.service;

import com.example.noteapplication.dto.AuthResponse;
import com.example.noteapplication.dto.LoginRequest;
import com.example.noteapplication.dto.RegisterRequest;
import com.example.noteapplication.models.ProviderType;
import com.example.noteapplication.models.User;
import com.example.noteapplication.repository.UserRepository;
import com.example.noteapplication.security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtils jwtUtils,
                       AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
        this.authenticationManager = authenticationManager;
    }

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email is already in use");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setName(request.getFullName());
        user.setProviderType(ProviderType.LOCAL);

        userRepository.save(user);

        String accessToken = jwtUtils.generateAccessToken(user.getEmail());
        String refreshToken = UUID.randomUUID().toString();

        return new AuthResponse(accessToken, refreshToken);
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        String accessToken = jwtUtils.generateAccessToken(request.getEmail());
        String refreshToken = UUID.randomUUID().toString();

        return new AuthResponse(accessToken, refreshToken);
    }
}