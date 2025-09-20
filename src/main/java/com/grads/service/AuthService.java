// UserService.java
package com.grads.service;

import com.grads.dto.AuthResponse;
import com.grads.dto.UserDto;
import com.grads.entity.User;
import com.grads.repository.UserRepository;
import com.grads.security.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService{

    public final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;


    public ResponseEntity<?> login(String email, String password) {
        // 1. Check if email exists
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty()) {
            return ResponseEntity
                    .status(421) // Custom: Email not found
                    .body("No user found with this email.");
        }

        User user = optionalUser.get();

        // 2. Check if account is enabled (optional)
        if (!user.isEnabled()) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("User account is disabled.");
        }

        // 3. Validate password manually
        if (!passwordEncoder.matches(password, user.getPassword())) {
            return ResponseEntity
                    .status(423) // Custom: Wrong password
                    .body("Invalid password.");
        }

        // 4. Generate JWT and return success
        String token = jwtUtil.generateToken(user);
        return ResponseEntity.ok(new AuthResponse(token, new UserDto(user)));
    }



    public ResponseEntity<?> createUser(String email, String password, String firstName, String lastName) {
        try {
            if (userRepository.existsByEmail(email)) {
                return ResponseEntity
                        .badRequest()
                        .body("Email already exists");
            }

            User user = new User();
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(password));
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setActive(true);
            user.setRole("ROLE_USER");

            User savedUser = userRepository.save(user);

            // Ensure user has an ID after save (save success criteria)
            if (savedUser != null && savedUser.getId() != null) {
                String token = jwtUtil.generateToken(savedUser);
                return ResponseEntity.ok(new AuthResponse(token, new UserDto(savedUser)));
            } else {
                return ResponseEntity
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("User could not be saved. Database error.");
            }

        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred: " + e.getMessage());
        }


    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User updateProfile(Long userId, String firstName, String lastName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setFirstName(firstName);
        user.setLastName(lastName);

        return userRepository.save(user);
    }

    public User changePassword(Long userId, String currentPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        return userRepository.save(user);
    }

    public User updateProfileImage(Long userId, String imageUrl) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setProfileImageUrl(imageUrl);
        return userRepository.save(user);
    }

    public UserDto convertToDto(User user) {
        UserDto dto = new UserDto(user.getEmail(), user.getFirstName(), user.getLastName(), user.getProfileImageUrl());
        return dto;
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }


    public User findUser(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if(user.isEmpty()){
            throw new IllegalArgumentException("Invalid token");
        }

        return user.get();
    }


}
