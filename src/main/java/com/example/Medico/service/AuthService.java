package com.example.Medico.service;

import com.example.Medico.model.User;
import com.example.Medico.repository.UserRepository;
import com.example.Medico.security.JwtTokenProvider;
import com.example.Medico.dto.AuthRequest;
import com.example.Medico.dto.AuthResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider tokenProvider;

    public AuthResponse login(AuthRequest request) throws Exception {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new Exception("User not found with username: " + request.getUsername()));

        if (!user.getActive()) {
            throw new Exception("User account is inactive");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new Exception("Invalid credentials");
        }

        String token = tokenProvider.generateToken(user.getUsername(), user.getRole(), user.getFullName());

        return new AuthResponse(token, user.getUsername(), user.getRole(), user.getFullName(), "Login successful");
    }

    public AuthResponse register(User user) throws Exception {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new Exception("Username already exists");
        }

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new Exception("Email already exists");
        }

        // Hash password
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setActive(true);

        User savedUser = userRepository.save(user);

        String token = tokenProvider.generateToken(savedUser.getUsername(), savedUser.getRole(), savedUser.getFullName());

        return new AuthResponse(token, savedUser.getUsername(), savedUser.getRole(), savedUser.getFullName(), "Registration successful");
    }
}
