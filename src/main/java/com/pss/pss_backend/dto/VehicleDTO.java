package com.pss.pss_backend.dto;

import com.pss.pss_backend.model.Vehicle;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class VehicleDTO {
    private Long vehicleId;
    private String make;
    private String model;
    private String licensePlate;
    private int seats;
    private LocalDateTime createdAt;
    private Long driverId;

    public VehicleDTO(){

    }

    public VehicleDTO(Vehicle vehicle) {
        this.vehicleId = vehicle.getVehicleId();
        this.make = vehicle.getMake();
        this.model = vehicle.getModel();
        this.licensePlate = vehicle.getLicensePlate();
        this.seats = vehicle.getSeats();
        this.createdAt = vehicle.getCreatedAt();
        this.driverId = vehicle.getDriver() != null ? vehicle.getDriver().getUserId() : null; // Assuming driver has a userId
    }
}
