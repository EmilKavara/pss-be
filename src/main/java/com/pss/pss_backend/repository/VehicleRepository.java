package com.pss.pss_backend.repository;

import com.pss.pss_backend.model.User;
import com.pss.pss_backend.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    Optional<Vehicle> findByMakeAndModelAndDriverAndLicensePlate(String make, String model, User driver, String licensePlate);
    Optional<Vehicle> findByDriver(User driver);
}

