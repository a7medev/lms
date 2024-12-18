package com.lms.notification;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {
    List<Notification> findAllByUserId(int userID);
    List<Notification> findAllByUserIdAndUnread(int userID, boolean unread);
    Optional<Notification> findById(int notificationID);
}
