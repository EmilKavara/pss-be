package com.pss.pss_backend.service;

import com.pss.pss_backend.model.Ride;
import com.pss.pss_backend.repository.RideRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RideStatusUpdaterService {

    @Autowired
    private RideRepository rideRepository;

    // Runs every 30 minutes to update ride statuses
    @Scheduled(cron = "0 */30 * * * *") // Every 30 minutes
    @Transactional
    public void updateFinishedRides() {
        LocalDateTime now = LocalDateTime.now();

        // Find active rides where departure time has passed
        List<Ride> overdueRides = rideRepository.findByStatusAndDepartureTimeBefore("active", now);

        for (Ride ride : overdueRides) {
            ride.setStatus("finished");
        }

        rideRepository.saveAll(overdueRides);
    }
}
