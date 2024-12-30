package com.pss.pss_backend.service;

import com.pss.pss_backend.dto.VehicleDTO;
import com.pss.pss_backend.model.User;
import com.pss.pss_backend.model.Vehicle;
import com.pss.pss_backend.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    public VehicleDTO updateVehicle(Long vehicleId, Vehicle updatedVehicle) {
        Optional<Vehicle> existingVehicleOptional = vehicleRepository.findById(vehicleId);

        if (existingVehicleOptional.isEmpty()) {
            throw new RuntimeException("Vehicle not found with ID: " + vehicleId);
        }

        Vehicle existingVehicle = existingVehicleOptional.get();

        if (!existingVehicle.getMake().equals(updatedVehicle.getMake())) {
            existingVehicle.setMake(updatedVehicle.getMake());
        }
        if (!existingVehicle.getLicensePlate().equals(updatedVehicle.getLicensePlate())) {
            existingVehicle.setLicensePlate(updatedVehicle.getLicensePlate());
        }
        if (existingVehicle.getSeats() != updatedVehicle.getSeats()) {
            existingVehicle.setSeats(updatedVehicle.getSeats());
        }
        if (!existingVehicle.getModel().equals(updatedVehicle.getModel())) {
            existingVehicle.setModel(updatedVehicle.getModel());
        }

        Vehicle savedVehicle = vehicleRepository.save(existingVehicle);
        return toDTO(savedVehicle);
    }

    public Optional<VehicleDTO> getVehicleById(Long vehicleId) {
        return vehicleRepository.findById(vehicleId).map(this::toDTO);
    }

    public List<VehicleDTO> getAllVehicles() {
        return vehicleRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public void deleteVehicle(Long vehicleId) {
        vehicleRepository.deleteById(vehicleId);
    }

    public Optional<VehicleDTO> getVehicleByDriver(User driver) {
        return vehicleRepository.findByDriver(driver).map(this::toDTO);
    }

    public int getMaxSeats(Long vehicleId) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new RuntimeException("Vehicle not found"));
        return vehicle.getSeats();
    }

    // Metoda za konverziju iz Vehicle u VehicleDTO
    private VehicleDTO toDTO(Vehicle vehicle) {
        VehicleDTO dto = new VehicleDTO();
        dto.setVehicleId(vehicle.getVehicleId());
        dto.setMake(vehicle.getMake());
        dto.setModel(vehicle.getModel());
        dto.setLicensePlate(vehicle.getLicensePlate());
        dto.setSeats(vehicle.getSeats());
        dto.setCreatedAt(vehicle.getCreatedAt());
        dto.setDriverId(vehicle.getDriver().getUserId());
        return dto;
    }
}
