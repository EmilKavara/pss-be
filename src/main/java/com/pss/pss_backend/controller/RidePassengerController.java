package com.pss.pss_backend.controller;

import com.pss.pss_backend.dto.RequestDTO;
import com.pss.pss_backend.model.RidePassenger;
import com.pss.pss_backend.service.RidePassengerService;
import com.pss.pss_backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/ride-passengers")
@CrossOrigin(origins = "http://localhost:4200")
public class RidePassengerController {

    @Autowired
    private RidePassengerService ridePassengerService;

    @Autowired
    private UserService userService;

    @GetMapping("/requests")
    public List<RequestDTO> getRequests() {
        // Get the logged-in username from the token
        String username = getLoggedInUsername();

        // Fetch the driverId from the UserService based on the username
        Long driverId = userService.getUserByUsername(username)
                .map(user -> user.getUserId().longValue())  // Convert userId to Long
                .orElseThrow(() -> new RuntimeException("Driver not found for username: " + username));

        // Use the driverId to get the requests for that driver
        return ridePassengerService.getRequestsForDriver(driverId);
    }

    @PostMapping("/requests/{requestId}/handle")
    public ResponseEntity<Map<String, String>> handleRequest(@PathVariable Long requestId, @RequestParam boolean isAccepted) {
        try {
            // Get the logged-in username from the token
            String username = getLoggedInUsername();

            // Fetch the driverId from the UserService based on the username
            Long driverId = userService.getUserByUsername(username)
                    .map(user -> user.getUserId().longValue())  // Convert userId to Long
                    .orElseThrow(() -> new RuntimeException("Driver not found for username: " + username));

            // Handle the request
            ridePassengerService.handleRequest(requestId, isAccepted);
            String message = isAccepted ? "Request approved successfully" : "Request denied successfully";

            // Return a JSON response with a message
            Map<String, String> response = new HashMap<>();
            response.put("message", message);
            return ResponseEntity.ok(response);
        } catch (RuntimeException ex) {
            // Return a JSON response with an error message
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", ex.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }


    private String getLoggedInUsername() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();  // Extract the username from JWT token
        } else {
            return principal.toString();  // If not an instance of UserDetails, return the principal as a string
        }
    }

}
