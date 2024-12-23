package com.lms.notification;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {
    List<Notification> findAllByUserId(Long userID);
    List<Notification> findAllByUserIdAndIsRead(Long userId, boolean isRead);
    Optional<Notification> findById(int notificationID);
}
