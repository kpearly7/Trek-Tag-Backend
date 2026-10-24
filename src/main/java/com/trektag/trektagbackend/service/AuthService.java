
package com.trektag.trektagbackend.service;

import com.trektag.trektagbackend.model.User;
import com.trektag.trektagbackend.repository.UserRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User register(
            String name,
            String email,
            String rawPassword,
            User.Role role) {

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name is required.");
        }

        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email is required.");
        }

        if (rawPassword == null || rawPassword.length() < 8) {
            throw new IllegalArgumentException(
                "Password must be at least 8 characters long.");
        }

        if (role == null) {
            throw new IllegalArgumentException("Account type is required.");
        }

        String cleanEmail = email.trim().toLowerCase();

        if (userRepository.existsByEmailIgnoreCase(cleanEmail)) {
            throw new IllegalArgumentException(
                "An account with this email already exists.");
        }

        User user = new User();
        user.setName(name.trim());
        user.setEmail(cleanEmail);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setRole(role);

        return userRepository.save(user);
    }

    public User login(String email, String rawPassword) {

        if (email == null || rawPassword == null) {
            throw new IllegalArgumentException(
                "Email and password are required.");
        }

        User user = userRepository
            .findByEmailIgnoreCase(email.trim())
            .orElseThrow(() -> new IllegalArgumentException(
                "Invalid email or password."));

        if (!passwordEncoder.matches(
                rawPassword, user.getPassword())) {
            throw new IllegalArgumentException(
                "Invalid email or password.");
        }

        return user;
    }
}
