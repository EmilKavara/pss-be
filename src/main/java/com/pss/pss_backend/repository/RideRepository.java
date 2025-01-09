package com.pss.pss_backend.repository;

import com.pss.pss_backend.model.Ride;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;


public interface RideRepository extends JpaRepository<Ride, Long> {
    List<Ride> findByStatus(String status);

    List<Ride> findByDepartureTimeBetween(LocalDateTime start, LocalDateTime end);

    List<Ride> findByStatusAndDepartureTimeBefore(String status, LocalDateTime time);

    List<Ride> findByStatusAndDriver_Username(String status, String username);

    List<Ride> findByDriver_Username(String username);

    List<Ride> findByDriver_UserIdAndDepartureTimeBetween(Long driverId, LocalDateTime now, LocalDateTime sevenDaysFromNow);

    List<Ride> findByDriver_UserId(Long driverId);
}
