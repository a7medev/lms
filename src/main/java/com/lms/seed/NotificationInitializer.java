package com.lms.seed;

import com.lms.notification.Notification;
import com.lms.notification.NotificationRepository;
import com.lms.user.User;
import com.lms.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.Month;

@Component
@RequiredArgsConstructor
public class NotificationInitializer implements CommandLineRunner {
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;

    @Override
    public void run(String... args) {
        createAndSaveNotification(1, "Hello World", true);
        createAndSaveNotification(2, "Hello Admin", false);
    }

    private void createAndSaveNotification(int id, String message, boolean unread) {
        User user = userRepository.findByEmail("admin@lms.com").orElseThrow();
        Notification notification = Notification
                .builder()
                .id(id)
                .message(message)
                .creationDate(LocalDateTime.of(1973, Month.OCTOBER, 6, 14, 0))
                .unread(unread)
                .user(user)
                .build();

        notificationRepository.save(notification);
    }

}
