package com.lms.enrollment;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/courses/{courseId}/enrollments")
public class EnrollmentController {
    private final EnrollmentService enrollmentService;

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

}