package com.pss.pss_backend.service;

import com.pss.pss_backend.model.Notification;
import com.pss.pss_backend.model.Ride;
import com.pss.pss_backend.model.RidePassenger;
import com.pss.pss_backend.repository.NotificationRepository;
import com.pss.pss_backend.repository.RideRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RideNotificationService {

    @Autowired
    private RideRepository rideRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private EmailService emailService;

    // Runs every hour to check for upcoming rides
    //@Scheduled(cron = "0 0 * * * *") // At the start of every hour
    @Scheduled(initialDelay = 0, fixedRate = Long.MAX_VALUE)
    @Transactional
    public void sendRideNotifications() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneHourFromNow = now.plusHours(1);
        LocalDateTime oneDayFromNow = now.plusDays(1);

        // Find rides happening in 1 hour and 1 day
        List<Ride> upcomingRides = rideRepository.findByDepartureTimeBetween(now, oneDayFromNow);

        for (Ride ride : upcomingRides) {
            for (RidePassenger participant : ride.getRidePassengers()) {
                String message = String.format("Reminder: Your ride from %s to %s departs at %s.",
                        ride.getOrigin(), ride.getDestination(), ride.getDepartureTime());

                // Send notification
                Notification notification = new Notification();
                notification.setUser(participant.getUser());
                notification.setMessage(message);
                notification.setStatus("unread");
                notificationRepository.save(notification);

                // Optionally send an email
                emailService.sendConfirmationEmail(
                        participant.getUser().getEmail(),
                        "Ride Reminder",
                        message
                );
            }
        }
    }
}
