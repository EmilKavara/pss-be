package com.pss.pss_backend.repository;

import com.pss.pss_backend.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByUser_UserId(Long userId);

    List<Notification> findByUser_UserIdAndStatus(Long userId, String status);

}

