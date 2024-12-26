package com.pss.pss_backend.controller;

import com.pss.pss_backend.model.User;
import com.pss.pss_backend.model.Vehicle;
import com.pss.pss_backend.service.UserService;
import com.pss.pss_backend.service.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/vehicles")
public class VehicleController {

    @Autowired
    private VehicleService vehicleService;

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<Vehicle> createVehicle(@RequestBody Vehicle vehicle) {
        try {
            Vehicle savedVehicle = vehicleService.saveVehicle(vehicle);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedVehicle);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/{vehicleId}")
    public ResponseEntity<Vehicle> updateVehicle(@PathVariable Long vehicleId, @RequestBody Vehicle updatedVehicle) {
        try {
            // Call the service method to update the vehicle
            Vehicle updated = vehicleService.updateVehicle(vehicleId, updatedVehicle);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);  // 404 Not Found if vehicle not found
        }
    }



    @GetMapping("/{vehicleId}")
    public Optional<Vehicle> getVehicleById(@PathVariable Long vehicleId) {
        return vehicleService.getVehicleById(vehicleId);
    }

    @GetMapping
    public List<Vehicle> getAllVehicles() {
        return vehicleService.getAllVehicles();
    }

    @DeleteMapping("/{vehicleId}")
    public void deleteVehicle(@PathVariable Long vehicleId) {
        vehicleService.deleteVehicle(vehicleId);
    }

    @GetMapping("/{vehicleId}/seats")
    public ResponseEntity<Integer> getMaxSeats(@PathVariable Long vehicleId) {
        try {
            int maxSeats = vehicleService.getMaxSeats(vehicleId);
            return ResponseEntity.ok(maxSeats);
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/driver")
    public ResponseEntity<Vehicle> getVehicleForDriver() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<User> driverOptional = userService.getUserUsername(username);

        if (driverOptional.isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }

        User driver = driverOptional.get();

        Optional<Vehicle> vehicleOptional = vehicleService.getVehicleByDriver(driver);
        System.out.println("Vehicle found: " + vehicleOptional);
        return vehicleOptional.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.noContent().build());
    }

}
