package com.lms.notification;

import com.lms.notification.email.EmailNotificationService;
import com.lms.user.User;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.LinkedList;
import java.util.List;

import static com.lms.util.AuthUtils.principalToUser;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final EmailNotificationService emailNotificationService;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public LinkedList<NotificationResponse> getNotifications(boolean unread, Principal currentUser) {
        LinkedList<NotificationResponse> notifications = new LinkedList<>();

        User user = principalToUser(currentUser);
        List<Notification> usersNotification;

        if (unread) {
            usersNotification = getAllUnreadNotifications(user);
        } else {
            usersNotification = getAllNotifications(user);
        }

        for (Notification notification : usersNotification) {
            NotificationResponse notificationRequest = NotificationResponse
                    .builder()
                    .notificationId(notification.getId())
                    .message(notification.getMessage())
                    .isRead(notification.isRead())
                    .userId(user.getId())
                    .build();
            notifications.add(notificationRequest);
        }

        return notifications;
    }

    private List<Notification> getAllNotifications(User currentUser) {
        return notificationRepository.findAllByUserId(currentUser.getId());
    }

    private List<Notification> getAllUnreadNotifications(User currentUser) {
        return notificationRepository.findAllByUserIdAndIsRead(currentUser.getId(), false);
    }

    public void readNotifications(List<Integer> notificationIds, Principal currentUser) {
        User user = principalToUser(currentUser);

        for (int notificationId : notificationIds) {
            Notification notification = notificationRepository.findById(notificationId).orElse(null);

            boolean updateNotification = notification != null
                    && !notification.isRead()
                    && user.getId() == notification.getUser().getId();

            if (updateNotification) {
                notification.setRead(true);
                notificationRepository.save(notification);
            }
        }
    }

    public void saveNotification(Notification notification, String subject) {
        notificationRepository.save(notification);
        sendEmailNotification(notification, subject);
    }

    public void saveNotifications(List<Notification> notifications, String subject) {
        notificationRepository.saveAll(notifications);

        for (Notification notification : notifications) {
            sendEmailNotification(notification, subject);
        }
    }

    private void sendEmailNotification(Notification notification, String subject) {
        try {
            String text = notification.getMessage();
            String email = notification.getUser().getEmail();
            emailNotificationService.sendEmail(email, subject, text);
        } catch (MessagingException e) {
            logger.error("Failed to send email notification with subject {} and notification:", subject);
            logger.error(notification.toString());
            logger.error(e.getMessage());
        }
    }
}
