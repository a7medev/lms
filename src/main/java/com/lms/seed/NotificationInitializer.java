package com.lms.seed;

import com.lms.notification.Notification;
import com.lms.notification.NotificationRepository;
import com.lms.user.User;
import com.lms.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(2)
@RequiredArgsConstructor
public class NotificationInitializer implements CommandLineRunner {
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;

    @Override
    public void run(String... args) {
        if (notificationRepository.count() == 0) {
            createAndSaveNotification("Hello World", true);
            createAndSaveNotification("Hello Admin", false);
        }
    }

    private void createAndSaveNotification(String message, boolean isRead) {
        User user = userRepository.findByEmail("admin@lms.com").orElseThrow();
        Notification notification = Notification
                .builder()
                .message(message)
                .isRead(isRead)
                .user(user)
                .build();

        notificationRepository.save(notification);
    }

}
