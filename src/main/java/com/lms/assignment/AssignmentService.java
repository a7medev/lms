package com.lms.assignment;

import com.lms.course.Course;
import com.lms.course.CourseRepository;
import com.lms.enrollment.Enrollment;
import com.lms.enrollment.EnrollmentRepository;
import com.lms.enrollment.EnrollmentState;
import com.lms.notification.Notification;
import com.lms.notification.NotificationService;
import com.lms.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

import static com.lms.util.AuthUtils.hasCourseAccess;

@Service
@RequiredArgsConstructor
public class AssignmentService {
    private final AssignmentRepository assignmentRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final NotificationService notificationService;
    private final CourseRepository courseRepository;

    public List<Assignment> getAssignments(Long courseId, User user, boolean upcoming) {
        Course course = courseRepository.findByCourseId(courseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found"));

        boolean hasAccess = hasCourseAccess(course, user, enrollmentRepository);

        if (!hasAccess) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }

        if (upcoming) {
            LocalDateTime now = LocalDateTime.now();
            return assignmentRepository.findAllByCourseCourseIdAndDueDateAfter(courseId, now);
        }
        return assignmentRepository.findAllByCourseCourseId(courseId);
    }

    public Assignment getAssignment(Long assignmentId, User user) {
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Assignment not found"));

        boolean hasAccess = hasCourseAccess(assignment.getCourse(), user, enrollmentRepository);

        if (!hasAccess) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }

        return assignment;
    }

    public Assignment createAssignment(Assignment assignment, User user) {
        if (assignment.getCourse().getInstructor().getId() != user.getId()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }

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
