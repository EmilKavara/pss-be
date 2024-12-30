package com.pss.pss_backend.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Table(name = "vehicles")
@Data
@ToString(exclude = {"driver"})
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vehicle_id")
    @JsonBackReference
    private Long vehicleId;

    @ManyToOne
    @JoinColumn(name = "driver_id", referencedColumnName = "user_id")
    private User driver;

    @Column(nullable = false)
    private String make;

    @Column(nullable = false)
    private String model;

    @Column(name ="license_plate", nullable = false, unique = true)
    private String licensePlate;

    @Column(nullable = false)
    private int seats;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

}

