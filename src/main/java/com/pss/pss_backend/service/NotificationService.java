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
        List<User> passengers = ride.getRidePassengers().stream().map(RidePassenger::getUser).toList();
        for (User passenger : passengers) {
            sendEmailNotification(passenger, ride, message);

            saveNotification(passenger, message);
        }
    }

    private void sendEmailNotification(User passenger, Ride ride, String message) {
        String emailSubject = "Important Ride Update";
        String emailBody = "<html>" +
                "<head>" +
                "<style>" +
                "body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }" +
                "table { width: 100%; border-collapse: collapse; margin-top: 20px; }" +
                "th, td { padding: 10px; text-align: left; border: 1px solid #ddd; }" +
                "th { background-color: #f4f4f4; font-weight: bold; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<h2>Dear " + passenger.getFullName() + ",</h2>" +
                "<p>" + message + "</p>" +
                "<h3>Ride Details</h3>" +
                "<table>" +
                "<tr><th>Start Location</th><td>" + ride.getOrigin() + "</td></tr>" +
                "<tr><th>Destination</th><td>" + ride.getDestination() + "</td></tr>" +
                "<tr><th>Departure Time</th><td>" + ride.getDepartureTime() + "</td></tr>" +
                "</table>" +
                "<p>If you have any questions, feel free to contact our support team.</p>" +
                "<p>Thank you for using our service.</p>" +
                "<p><strong>Best regards,</strong><br>The Ride Team</p>" +
                "</body>" +
                "</html>";

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
