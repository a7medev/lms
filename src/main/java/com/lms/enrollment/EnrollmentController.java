package com.lms.enrollment;

import com.lms.notification.Notification;
import com.lms.notification.NotificationService;
import com.lms.user.User;
import com.lms.user.UserRepository;
import com.lms.user.UserService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/courses/{courseId}/enrollments")
public class EnrollmentController {
    private final UserService userService;
    private final EnrollmentService enrollmentService;
    private final NotificationService notificationService;
    private final UserRepository userRepository;

    @PreAuthorize("hasAnyAuthority('ADMIN','INSTRUCTOR')")
    @GetMapping
    public List<Enrollment> getAllEnrollments(@PathVariable Long courseId) {
        return enrollmentService.getAllEnrollments(courseId);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','INSTRUCTOR')")
    @GetMapping("/{enrollmentId}")
    public Enrollment getEnrollment(@PathVariable Long courseId, @PathVariable Long enrollmentId) {
        return enrollmentService.getEnrollment(courseId, enrollmentId);
    }

    @PostMapping
    public ResponseEntity<Enrollment> createEnrollment(@PathVariable Long courseId, Principal currentUser) {
        Enrollment savedEnrollment = enrollmentService.createEnrollment(courseId, currentUser);

        // Sending Notification to instructor
//        if (savedEnrollment != null) {
//            Integer instructorId = savedEnrollment.getCourse().getInstructorId();
//            userRepository.findById(instructorId).ifPresent(instructor -> {
//                try {
//                    sendEnrollmentNotification(instructor, savedEnrollment);
//                } catch (MessagingException e) {
//                    throw new RuntimeException(e);
//                }
//            });
//        }
//
        return ResponseEntity.ok(savedEnrollment);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','INSTRUCTOR')")
    @PutMapping("/{enrollmentId}")
    public ResponseEntity<Enrollment> updateEnrollmentState(
            @PathVariable Long courseId,
            @PathVariable Long enrollmentId,
            @RequestBody EnrollmentUpdateRequest updateRequest) {
        Enrollment updatedEnrollment = enrollmentService.updateEnrollmentState(courseId, enrollmentId, updateRequest);
        return ResponseEntity.ok(updatedEnrollment);
    }

    // Utility functions
//
//    private void sendEnrollmentNotification(User instructor, Enrollment savedEnrollment) throws MessagingException {
//        Notification notification = new Notification();
//        notification.setUser(instructor);
//
//        String message = createNotificationMessage(savedEnrollment);
//        notification.setMessage(message);
//
//        // Save the notification
//        notificationService.saveNotification(notification, "New Enrollment Pending: " + savedEnrollment.getCourse().getTitle());
//    }
//
//    private String createNotificationMessage(Enrollment savedEnrollment) {
//        return "A new enrollment request has been submitted by " + savedEnrollment.getUser().getName()
//                + " for your course: " + savedEnrollment.getCourse().getTitle()
//                + ". The enrollment is currently pending approval.";
//    }

};