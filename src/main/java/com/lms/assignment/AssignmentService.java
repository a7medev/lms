package com.lms.assignment;

import com.lms.course.Course;
import com.lms.enrollment.Enrollment;
import com.lms.enrollment.EnrollmentRepository;
import com.lms.enrollment.EnrollmentState;
import com.lms.notification.Notification;
import com.lms.notification.NotificationService;
import com.lms.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AssignmentService {
    private final AssignmentRepository assignmentRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final NotificationService notificationService;

    public List<Assignment> getAssignments(Long courseId, boolean upcoming) {
        if (upcoming) {
            LocalDateTime now = LocalDateTime.now();
            return assignmentRepository.findAllByCourseCourseIdAndDueDateAfter(courseId, now);
        }
        return assignmentRepository.findAllByCourseCourseId(courseId);
    }

    public Optional<Assignment> getAssignment(Long assignmentId) {
        return assignmentRepository.findById(assignmentId);
    }

    public Assignment createAssignment(Assignment assignment) {
        Assignment createdAssignment = assignmentRepository.save(assignment);
        sendCreateAssignmentNotification(createdAssignment);
        return createdAssignment;
    }

    private void sendCreateAssignmentNotification(Assignment assignment) {
        Course course = assignment.getCourse();
        List<Enrollment> activeEnrollments = enrollmentRepository.findAllByCourseCourseIdAndEnrollmentState(
                course.getCourseId(),
                EnrollmentState.ACTIVE
        );

        String subject = "LMS - New Assignment";
        List<Notification> notifications = activeEnrollments.stream()
                .map(enrollment -> {
                    User student = enrollment.getUser();
                    String message = String.format("New assignment \"%s\" in the \"%s\" course.", assignment.getTitle(), course.getTitle());

                    return Notification.builder()
                            .message(message)
                            .user(student)
                            .build();
                })
                .toList();

        notificationService.saveNotifications(notifications, subject);
    }

}
