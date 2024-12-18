package com.lms.notification;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {
    List<Notification> findAllByUserId(int userID);
    List<Notification> findAllByUserIdAndIsRead(int userID, boolean isRead);
    Optional<Notification> findById(int notificationID);
}
