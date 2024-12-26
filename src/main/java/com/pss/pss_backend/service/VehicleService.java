package com.pss.pss_backend.service;

import com.pss.pss_backend.model.User;
import com.pss.pss_backend.model.Vehicle;
import com.pss.pss_backend.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VehicleService {

    @Autowired
    private VehicleRepository vehicleRepository;

    public Vehicle saveVehicle(Vehicle vehicle) {
        Optional<Vehicle> existingVehicle = vehicleRepository.findByMakeAndModelAndDriverAndLicensePlate(
                vehicle.getMake(), vehicle.getModel(), vehicle.getDriver(), vehicle.getLicensePlate()
        );

        if (existingVehicle.isPresent()) {
            throw new IllegalArgumentException("Vehicle with this license plate already exists for this user.");
        }

        return vehicleRepository.save(vehicle);
    }

    public Vehicle updateVehicle(Long vehicleId, Vehicle updatedVehicle) {
        // Find the existing vehicle by ID
        Optional<Vehicle> existingVehicleOptional = vehicleRepository.findById(vehicleId);

        if (existingVehicleOptional.isEmpty()) {
            throw new RuntimeException("Vehicle not found with ID: " + vehicleId);
        }

        Vehicle existingVehicle = existingVehicleOptional.get();

        existingVehicle.setLicensePlate(updatedVehicle.getLicensePlate());
        existingVehicle.setSeats(updatedVehicle.getSeats());
        existingVehicle.setModel(updatedVehicle.getModel());

        // Save the updated vehicle back to the repository
        return vehicleRepository.save(existingVehicle);
    }


    public Optional<Vehicle> getVehicleById(Long vehicleId) {
        return vehicleRepository.findById(vehicleId);
    }

    public List<Vehicle> getAllVehicles() {
        return vehicleRepository.findAll();
    }

    public void deleteVehicle(Long vehicleId) {
        vehicleRepository.deleteById(vehicleId);
    }

    public int getMaxSeats(Long vehicleId) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new RuntimeException("Vehicle not found"));
        return vehicle.getSeats();
    }

    public Optional<Vehicle> getVehicleByDriver(User driver) {
        return vehicleRepository.findByDriver(driver);
    }

}
