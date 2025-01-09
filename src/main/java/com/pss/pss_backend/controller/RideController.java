package com.pss.pss_backend.controller;

import com.pss.pss_backend.dto.DelayRequest;
import com.pss.pss_backend.dto.RideDTO;
import com.pss.pss_backend.dto.RideGraphData;
import com.pss.pss_backend.dto.RideStatusDTO;
import com.pss.pss_backend.model.Ride;
import com.pss.pss_backend.responses.ApiResponse;
import com.pss.pss_backend.service.RideService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

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
    public ResponseEntity<Map<String, String>> cancelRide(@PathVariable Long id) {
        rideService.cancelRide(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Ride cancelled successfully");
        return ResponseEntity.ok(response);
    }


    @PostMapping("/{id}/delay")
    public ResponseEntity<?> reportDelay(@PathVariable Long id, @RequestBody DelayRequest delayRequest) {
        try {
            rideService.reportDelay(id, delayRequest.getNewDepartureTime());
            return ResponseEntity.ok(new ApiResponse("Delay reported successfully", true));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse("Error reporting delay", false));
        }
    }


    @GetMapping("/filter-advanced")
    public List<RideStatusDTO> getRidesByFilters(
            @RequestParam String status,
            @RequestParam(required = false) String destination,
            @RequestParam(required = false, defaultValue = "1") int minSeats,
            @RequestParam(required = false) String sortBy) {
        if (minSeats < 1) {
            minSeats = 1;
        }
        return rideService.getFilteredRides(status, destination, minSeats, sortBy);
    }

    @PostMapping("/{rideId}/cancel-passenger")
    public ResponseEntity<Void> cancelRidePassenger(@PathVariable Long rideId, @RequestBody Map<String, Long> payload) {
        Long passengerId = payload.get("passengerId");
        try {
            rideService.passengerCancelRide(rideId, passengerId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/driver/{driverId}/planned-rides")
    public ResponseEntity<List<Ride>> getPlannedRides(
            @PathVariable Long driverId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        List<Ride> plannedRides = rideService.getPlannedRides(driverId, startDate.atStartOfDay(), endDate.atTime(23, 59, 59));
        return ResponseEntity.ok(plannedRides);
    }

    @GetMapping("/driver/{driverId}/ride-stats")
    public ResponseEntity<List<RideGraphData>> getRideStats(@PathVariable Long driverId) {

        List<Ride> rides = rideService.getRidesByDriver(driverId);

        List<RideGraphData> monthlyStats = groupRidesByMonth(rides);

        return ResponseEntity.ok(monthlyStats);
    }

    private List<RideGraphData> groupRidesByMonth(List<Ride> rides) {

        Map<String, Long> monthlyStats = rides.stream()
                .collect(Collectors.groupingBy(ride -> String.valueOf(ride.getDepartureTime().getMonth()), Collectors.counting()));

        List<RideGraphData> graphData = new ArrayList<>();
        monthlyStats.forEach((month, count) -> graphData.add(new RideGraphData(month, count)));
        return graphData;
    }




}
