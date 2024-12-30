package com.pss.pss_backend.controller;

import com.pss.pss_backend.dto.DelayRequest;
import com.pss.pss_backend.dto.RideDTO;
import com.pss.pss_backend.dto.RideStatusDTO;
import com.pss.pss_backend.model.Ride;
import com.pss.pss_backend.service.RideService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/rides")
@CrossOrigin(origins = "http://localhost:4200")
public class RideController {

    @Autowired
    private RideService rideService;

    @PostMapping("/ride")
    public ResponseEntity<Map<String, String>> createRide(@RequestBody RideDTO rideDTO) {
        try {
            String username = getLoggedInUsername();
            rideService.createRide(rideDTO, username);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Ride created successfully");

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException ex) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", ex.getMessage());

            return ResponseEntity.badRequest().body(errorResponse);
        }
    }



    private String getLoggedInUsername() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        } else {
            return principal.toString();
        }
    }

    @GetMapping("/{rideId}")
    public Optional<Ride> getRideById(@PathVariable Long rideId) {
        return rideService.getRideById(rideId);
    }

    @GetMapping
    public List<Ride> getAllRides() {
        return rideService.getAllRides();
    }

    @DeleteMapping("/{rideId}")
    public void deleteRide(@PathVariable Long rideId) {
        rideService.deleteRide(rideId);
    }

    @GetMapping("/filter")
    public List<RideStatusDTO> getRidesByStatusAndUser(
            @RequestParam String status) {
        String username = getLoggedInUsername();
        return rideService.getRidesByStatusAndUser(status, username);
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<?> cancelRide(@PathVariable Long id) {
        rideService.cancelRide(id);
        return ResponseEntity.ok("Ride cancelled successfully");
    }

    @PostMapping("/{id}/delay")
    public ResponseEntity<?> reportDelay(@PathVariable Long id, @RequestBody DelayRequest delayRequest) {
        rideService.reportDelay(id, delayRequest.getNewDepartureTime());
        return ResponseEntity.ok("Delay reported successfully");
    }

    @GetMapping("/filter-advanced")
    public List<RideStatusDTO> getRidesByFilters(
            @RequestParam String status,
            @RequestParam(required = false) String destination,
            @RequestParam(required = false, defaultValue = "0") int minSeats,
            @RequestParam(required = false) String sortBy) {
        return rideService.getFilteredRides(status, destination, minSeats, sortBy);
    }


}
