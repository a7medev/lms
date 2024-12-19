package com.lms.enrollment;

import com.lms.course.Course;
import com.lms.course.CourseService;
import com.lms.user.User;
import com.lms.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;

@Service
public class EnrollmentService {
    EnrollmentRepository enrollmentRepository;
    final CourseService courseService;
    private final UserService userService;

    @Autowired
    public EnrollmentService(EnrollmentRepository enrollmentRepository, CourseService courseService, UserService userService) {
        this.enrollmentRepository = enrollmentRepository;
        this.courseService = courseService;
        this.userService = userService;
    }

    public List<Enrollment> getAllEnrollments() {
        return enrollmentRepository.findAll();
    }

    public Enrollment CourseEnrollment(Long courseId, Principal currentUser){
        Enrollment enrollment = new Enrollment();
        Course course = courseService.getCourseById(courseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found"));
        User user = userService.getUser(currentUser);
        enrollment.setCourse(course);
        enrollment.setUser(user);
        enrollment.setState(State.PENDING);
        return enrollmentRepository.save(enrollment);
    }

    public Enrollment updateEnrollmentState(Long courseId, Long enrollmentId, EnrollmentUpdateRequest updateRequest) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Enrollment not found"));

        if (!enrollment.getCourse().getCourseId().equals(courseId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Enrollment does not belong to the specified course");
        }

        System.out.println("Request body: " + updateRequest.toString());
        if (updateRequest.isAccepted()) {
            enrollment.setState(State.ACTIVE);
            enrollment.setCancellationReason(null);
        } else {
            enrollment.setState(State.CANCELLED);
            enrollment.setCancellationReason(updateRequest.getCancellationReason());
        }

        return enrollmentRepository.save(enrollment);
    }


    public Enrollment getEnrollment(Long courseId, Long enrollmentId) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Enrollment not found"));
        if (!enrollment.getCourse().getCourseId().equals(courseId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Course ID does not match the enrollment's course");
        }
        return enrollment;
    }
}
