package com.lms.enrollment;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping(path = "/courses/{courseId}/enrollments")
public class EnrollmentController {
    EnrollmentService enrollmentService;

    public EnrollmentController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping
    public List<Enrollment> getAllEnrollments(@PathVariable Long courseId) {
        return enrollmentService.getAllEnrollments();
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','INSTRUCTOR')")
    @GetMapping("/{enrollmentId}")
    public Enrollment getEnrollment(@PathVariable Long courseId, @PathVariable Long enrollmentId) {
        return enrollmentService.getEnrollment(courseId, enrollmentId);
    }

    @PostMapping
    public ResponseEntity<Enrollment> CourseEnrollment(@PathVariable Long courseId, Principal currentUser) {
        Enrollment savedEnrollment = enrollmentService.CourseEnrollment(courseId, currentUser);
        return ResponseEntity.ok(savedEnrollment);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','INSTRUCTOR')")
    @PutMapping("/{enrollmentId}")
    public ResponseEntity<Enrollment> updateEnrollmentState(
            @PathVariable Long courseId,
            @PathVariable Long enrollmentId,
            @RequestBody EnrollmentUpdateRequest updateRequest) {
        Enrollment updatedEnrollment = enrollmentService.updateEnrollmentState(courseId,enrollmentId, updateRequest);
        return ResponseEntity.ok(updatedEnrollment);
    }

};