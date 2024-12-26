package com.pss.pss_backend.service;

import com.pss.pss_backend.model.Ride;
import com.pss.pss_backend.model.User;
import com.pss.pss_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public Optional<User> getUserById(Long userId) {
        return userRepository.findById(userId);
    }

    public Optional<User> getUserUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public List<User> getAllUsers() {
        System.out.println(userRepository.findAll());
        return userRepository.findAll();
    }

    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

    public List<Ride> getUserRideHistory(String username) {
        return userRepository.findByUsername(username)
                .map(User::getRideHistory)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
