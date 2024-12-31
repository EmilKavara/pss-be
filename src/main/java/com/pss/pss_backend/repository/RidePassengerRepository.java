package com.pss.pss_backend.repository;

import com.pss.pss_backend.model.RidePassenger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RidePassengerRepository extends JpaRepository<RidePassenger, Long> {

    List<RidePassenger> findByRide_RideIdAndRoleAndStatus(Long rideId, String role, String status);

    List<RidePassenger> findByRide_RideIdAndRole(Long rideId, String role);

    List<RidePassenger> findByRide_Driver_UserIdAndRoleAndStatus(Long driverId, String role, String status);

    List<RidePassenger> findByUser_UserId(Long userId);

    void deleteByRide_RideId(Long rideId);

    Optional<RidePassenger> findByRide_RideIdAndUser_UserIdAndRole(Long rideId, Long userId, String role);

    List<RidePassenger> findByUser_UserIdAndStatusIn(Long userId, List<String> statuses);


}
