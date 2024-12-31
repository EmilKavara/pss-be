package com.pss.pss_backend.controller;

import com.pss.pss_backend.model.Notification;
import com.pss.pss_backend.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @PostMapping
    public Notification createNotification(@RequestBody Notification notification) {
        return notificationService.saveNotification(notification);
    }

    @GetMapping("/{notificationId}")
    public ResponseEntity<Notification> getNotificationById(@PathVariable Long notificationId) {
        Optional<Notification> notification = notificationService.getNotificationById(notificationId);

        if (notification.isPresent()) {
            return ResponseEntity.ok(notification.get()); // Return OK with the notification data
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // Return NOT_FOUND if notification doesn't exist
        }
    }


    @GetMapping
    public List<Notification> getAllNotifications() {
        return notificationService.getAllNotifications();
    }

    @DeleteMapping("/{notificationId}")
    public void deleteNotification(@PathVariable Long notificationId) {
        notificationService.deleteNotification(notificationId);
    }

    @PostMapping("/mark-as-read")
    public ResponseEntity<?> markNotificationsAsRead(@RequestBody List<Long> notificationIds) {
        notificationService.markAsRead(notificationIds);
        return ResponseEntity.ok("Notifications marked as read");
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Notification>> getNotifications(@PathVariable Long userId) {
        return ResponseEntity.ok(notificationService.getUserNotifications(userId));
    }

}
