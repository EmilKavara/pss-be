package com.pss.pss_backend.service;

import com.pss.pss_backend.model.Notification;
import com.pss.pss_backend.model.Ride;
import com.pss.pss_backend.model.RidePassenger;
import com.pss.pss_backend.model.User;
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

    //@Scheduled(cron = "0 0 * * * *") // At the start of every hour
    @Scheduled(initialDelay = 0, fixedRate = Long.MAX_VALUE)
    @Transactional
    public void sendRideNotifications() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneHourFromNow = now.plusHours(1);
        LocalDateTime oneDayFromNow = now.plusDays(1);

        List<Ride> upcomingRides = rideRepository.findByDepartureTimeBetween(now, oneDayFromNow);

        for (Ride ride : upcomingRides) {
            for (RidePassenger participant : ride.getRidePassengers()) {
                String message = String.format(
                        "This is a friendly reminder that your ride is scheduled to depart soon. Please make necessary preparations."
                );

                Notification notification = new Notification();
                notification.setUser(participant.getUser());
                notification.setMessage(message);
                notification.setStatus("UNREAD");
                notificationRepository.save(notification);

                String emailSubject = "Upcoming Ride Reminder";
                String emailBody = buildRideReminderEmail(participant.getUser(), ride);
                emailService.sendConfirmationEmail(participant.getUser().getEmail(), emailSubject, emailBody);
            }
        }
    }

    private String buildRideReminderEmail(User participant, Ride ride) {
        return String.format(
                "<html>" +
                        "<head>" +
                        "<style>" +
                        "body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }" +
                        "table { width: 100%%; border-collapse: collapse; margin-top: 20px; }" +
                        "th, td { padding: 10px; text-align: left; border: 1px solid #ddd; }" +
                        "th { background-color: #f4f4f4; font-weight: bold; }" +
                        "</style>" +
                        "</head>" +
                        "<body>" +
                        "<h2>Dear %s,</h2>" +
                        "<p>This is a reminder for your upcoming ride. Below are the details:</p>" +
                        "<h3>Ride Details</h3>" +
                        "<table>" +
                        "<tr><th>Start Location</th><td>%s</td></tr>" +
                        "<tr><th>Destination</th><td>%s</td></tr>" +
                        "<tr><th>Departure Time</th><td>%s</td></tr>" +
                        "</table>" +
                        "<p>Please be on time at the departure location. If you have any questions, feel free to contact our support team.</p>" +
                        "<p>Thank you for using our service.</p>" +
                        "<p><strong>Best regards,</strong><br>The Ride Team</p>" +
                        "</body>" +
                        "</html>",
                participant.getFullName(),
                ride.getOrigin(),
                ride.getDestination(),
                ride.getDepartureTime()
        );
    }

}
