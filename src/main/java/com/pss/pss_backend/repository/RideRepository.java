package com.pss.pss_backend.repository;

import com.pss.pss_backend.model.Ride;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RideRepository extends JpaRepository<Ride, Long> {
}
