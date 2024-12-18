package com.lms.enrollment;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping(path = "/courses/{courseId}/enrollments")
public class EnrollmentController {
    EnrollmentService enrollmentService;

    public EnrollmentController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    @PostMapping
    public ResponseEntity<Enrollment> CourseEnrollment(@PathVariable Long courseId, Principal currentUser) {
        Enrollment savedEnrollment = enrollmentService.CourseEnrollment(courseId, currentUser);
        return ResponseEntity.ok(savedEnrollment);
    }

};