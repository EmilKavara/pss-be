package com.pss.pss_backend.service;

import com.pss.pss_backend.model.Notification;
import com.pss.pss_backend.model.Ride;
import com.pss.pss_backend.model.RidePassenger;
import com.pss.pss_backend.model.User;
import com.pss.pss_backend.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private EmailService emailService;

    public Notification saveNotification(Notification notification) {
        return notificationRepository.save(notification);
    }

    public Optional<Notification> getNotificationById(Long notificationId) {
        return notificationRepository.findById(notificationId);
    }

    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }

    public void deleteNotification(Long notificationId) {
        notificationRepository.deleteById(notificationId);
    }

    public void notifyPassengers(Ride ride, String message) {
        List<User> passengers = ride.getRidePassengers().stream().map(RidePassenger::getUser).toList(); // Assuming you have a method to fetch passengers
        for (User passenger : passengers) {
            // Send an email notification
            sendEmailNotification(passenger, ride, message);

            // Optionally save the notification in the database
            saveNotification(passenger, message);
        }
    }

    private void sendEmailNotification(User passenger, Ride ride, String message) {
        String emailSubject = "Ride Notification";
        String emailBody = "<html>"
                + "<body>"
                + "<p>Dear " + passenger.getFullName() + ",</p>"
                + "<p>" + message + "</p>"
                + "<table>"
                + "<tr><th>Start Location</th><td>" + ride.getOrigin() + "</td></tr>"
                + "<tr><th>Destination</th><td>" + ride.getDestination() + "</td></tr>"
                + "<tr><th>Departure Time</th><td>" + ride.getDepartureTime() + "</td></tr>"
                + "</table>"
                + "<p>Thank you for using our service.</p>"
                + "</body>"
                + "</html>";
        emailService.sendConfirmationEmail(passenger.getEmail(), emailSubject, emailBody);
    }

    private void saveNotification(User passenger, String message) {
        Notification notification = new Notification();
        notification.setUser(passenger);
        notification.setMessage(message);
        notification.setStatus("UNREAD");
        notificationRepository.save(notification);
    }
}
