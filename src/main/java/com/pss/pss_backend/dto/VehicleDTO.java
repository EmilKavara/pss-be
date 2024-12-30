package com.pss.pss_backend.dto;

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
}
