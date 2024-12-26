package com.pss.pss_backend.service;

import com.pss.pss_backend.dto.LoginUserDTO;
import com.pss.pss_backend.dto.RegisterUserDTO;
import com.pss.pss_backend.model.User;
import com.pss.pss_backend.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    public AuthenticationService(
            UserRepository userRepository,
            AuthenticationManager authenticationManager,
            PasswordEncoder passwordEncoder
    ) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User signup(RegisterUserDTO input) {
        User existingUser = userRepository.findByEmail(input.getEmail()).orElse(null);
        if (existingUser != null) {
            throw new IllegalArgumentException("Email is already in use.");
        }

        User user = new User();
        user.setFullName(input.getFullName());
        user.setEmail(input.getEmail());
        user.setRole(input.getRole());
        user.setUsername(generateUsernameFromFullName(input.getFullName()));
        user.setPassword(passwordEncoder.encode(input.getPassword()));

        return userRepository.save(user);
    }




    private String generateUsernameFromFullName(String fullName) {
        String[] nameParts = fullName.trim().split("\\s+");

        if (nameParts.length > 1) {
            return nameParts[0].toLowerCase() + "." + nameParts[nameParts.length - 1].toLowerCase();
        } else {
            return nameParts[0].toLowerCase();
        }
    }



    public User authenticate(LoginUserDTO input) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        input.getUsername(),
                        input.getPassword()
                )
        );

        return userRepository.findByUsername(input.getUsername())
                .orElseThrow();
    }
}
