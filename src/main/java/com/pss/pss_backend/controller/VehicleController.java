package com.pss.pss_backend.controller;

import com.pss.pss_backend.dto.VehicleDTO;
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
    public ResponseEntity<VehicleDTO> createVehicle(@RequestBody Vehicle vehicle) {
        try {
            Vehicle savedVehicle = vehicleService.saveVehicle(vehicle);
            VehicleDTO vehicleDTO = vehicleService.getVehicleById(savedVehicle.getVehicleId()).orElseThrow();
            return ResponseEntity.status(HttpStatus.CREATED).body(vehicleDTO);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/{vehicleId}")
    public ResponseEntity<VehicleDTO> updateVehicle(@PathVariable Long vehicleId, @RequestBody Vehicle updatedVehicle) {
        try {
            VehicleDTO updated = vehicleService.updateVehicle(vehicleId, updatedVehicle);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/{vehicleId}")
    public ResponseEntity<VehicleDTO> getVehicleById(@PathVariable Long vehicleId) {
        Optional<VehicleDTO> vehicleDTO = vehicleService.getVehicleById(vehicleId);
        return vehicleDTO.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<VehicleDTO> getAllVehicles() {
        return vehicleService.getAllVehicles();
    }

    @DeleteMapping("/{vehicleId}")
    public ResponseEntity<Void> deleteVehicle(@PathVariable Long vehicleId) {
        vehicleService.deleteVehicle(vehicleId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/driver")
    public ResponseEntity<VehicleDTO> getVehicleForDriver() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<com.pss.pss_backend.model.User> driverOptional = userService.getUserByUsername(username);

        if (driverOptional.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        com.pss.pss_backend.model.User driver = driverOptional.get();
        Optional<VehicleDTO> vehicleOptional = vehicleService.getVehicleByDriver(driver);

        return vehicleOptional.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.noContent().build());
    }
}
