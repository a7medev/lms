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

    public Enrollment CourseEnrollment(Long courseId, Principal currentUser){
        Enrollment enrollment = new Enrollment();
        Course course = courseService.getCourseById(courseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found"));
        User user = userService.getUser(currentUser);

        System.out.println("Current User: " + user.getId());

        enrollment.setCourse(course);
        enrollment.setUser(user);
        enrollment.setState(State.PENDING);
        enrollment.setCancellationReason("");
        return enrollmentRepository.save(enrollment);
    }

}
