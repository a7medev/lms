package com.lms.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<List<NotificationResponse>> getNotifications(@RequestParam(name = "unread") boolean unread, Principal currentUser) {
        return ResponseEntity.ok(notificationService.getNotifications(unread, currentUser));
    }

    @PutMapping("/read")
    public ResponseEntity<String> readNotifications(@RequestBody List<Integer> notificationIds, Principal currentUser) {
        notificationService.readNotifications(notificationIds, currentUser);
        return ResponseEntity.ok().body("Read Notifications Successfully");
    }
}
