package com.pss.pss_backend.service;

import com.pss.pss_backend.dto.RideDTO;
import com.pss.pss_backend.dto.RideHistoryDTO;
import com.pss.pss_backend.dto.UserProfileDTO;
import com.pss.pss_backend.model.Ride;
import com.pss.pss_backend.model.User;
import com.pss.pss_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    public Optional<User> getUserByUsername(String username) {
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

    public UserProfileDTO toUserProfileDTO(User user) {
        UserProfileDTO dto = new UserProfileDTO();
        dto.setUserId(user.getUserId());
        dto.setUsername(user.getUsername());
        dto.setFullName(user.getFullName());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        List<RideHistoryDTO> rideHistory = user.getRideHistory().stream()
                .map(this::toRideDTO)
                .collect(Collectors.toList());

        dto.setRideHistory(rideHistory);
        return dto;
    }

    private RideHistoryDTO toRideDTO(Ride ride) {
        RideHistoryDTO rideDTO = new RideHistoryDTO();
        rideDTO.setRideId(ride.getRideId());
        rideDTO.setDepartureTime(ride.getDepartureTime());
        rideDTO.setDestination(ride.getDestination());
        rideDTO.setOrigin(ride.getOrigin());
        rideDTO.setStatus(ride.getStatus());
        return rideDTO;
    }
}
