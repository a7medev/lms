package com.lms.enrollment;

import com.lms.course.Course;
import com.lms.course.CourseService;
import com.lms.notification.Notification;
import com.lms.notification.NotificationService;
import com.lms.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;

import static com.lms.util.AuthUtils.principalToUser;

@RequiredArgsConstructor
@Service
public class EnrollmentService {
    private final EnrollmentRepository enrollmentRepository;
    private final CourseService courseService;
    private final NotificationService notificationService;

    public List<Enrollment> getAllEnrollments(Long courseId) {
        return enrollmentRepository.findAllByCourseCourseId(courseId);
    }

    public Enrollment getEnrollment(Long courseId, Long enrollmentId) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Enrollment not found"));
        if (!enrollment.getCourse().getCourseId().equals(courseId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Course ID does not match the enrollment's course");
        }
        return enrollment;
    }

    public Enrollment createEnrollment(Long courseId, Principal currentUser) {
        Course course = courseService.getCourseById(courseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found"));
        User user = principalToUser(currentUser);
        Enrollment enrollment = Enrollment.builder()
                .course(course)
                .user(user)
                .enrollmentState(EnrollmentState.PENDING)
                .build();
        sendEnrollmentNotification(course, enrollment);
        return enrollmentRepository.save(enrollment);
    }

    public Enrollment updateEnrollmentState(Long courseId, Long enrollmentId, EnrollmentUpdateRequest updateRequest) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Enrollment not found"));

        if (!enrollment.getCourse().getCourseId().equals(courseId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Enrollment does not belong to the specified course");
        }

        if (updateRequest.isAccepted()) {
            enrollment.setEnrollmentState(EnrollmentState.ACTIVE);
            enrollment.setCancellationReason(null);
        } else {
            enrollment.setEnrollmentState(EnrollmentState.CANCELLED);
            enrollment.setCancellationReason(updateRequest.getCancellationReason());
        }

        sendEnrollmentStateNotification(enrollment);

        return enrollmentRepository.save(enrollment);
    }

    private void sendEnrollmentNotification(Course course, Enrollment savedEnrollment) {
        User instructor = course.getInstructor();
        Notification notification = Notification.builder()
            .user(instructor)
            .message(createNotificationMessage(savedEnrollment))
            .build();

        notificationService.saveNotification(notification,
                "New Enrollment Pending: " + course.getTitle());
    }

    private void sendEnrollmentStateNotification(Enrollment enrollment) {
        Course course = enrollment.getCourse();

        String message = switch (enrollment.getEnrollmentState()) {
            case ACTIVE -> "Your enrollment in course \"" + course.getTitle() + "\" has been approved.";
            case CANCELLED -> "Your enrollment in course \"" + course.getTitle() + "\" has been cancelled.\nReason: " + enrollment.getCancellationReason();
            case PENDING -> null;
        };

        Notification notification = Notification.builder()
                .user(enrollment.getUser())
                .message(message)
                .build();

        notificationService.saveNotification(notification, "Enrollment state update: " + course.getTitle());
    }

    private String createNotificationMessage(Enrollment savedEnrollment) {
        return "A new enrollment request has been submitted by "
                + savedEnrollment.getUser().getName()
                + " for your course: " + savedEnrollment.getCourse().getTitle()
                + ". The enrollment is currently pending approval.";
    }

}
