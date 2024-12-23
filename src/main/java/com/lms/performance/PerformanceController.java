package com.lms.performance;

import com.lms.course.Course;
import com.lms.course.CourseRepository;
import com.lms.user.Role;
import com.lms.user.User;
import com.lms.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.Optional;

@RestController
@RequestMapping("/courses/{courseId}/analytics")
public class PerformanceController {

    private final PerformanceService performanceService;
    private final CourseRepository courseRepository;
    private final UserService userService;

    @Autowired
    public PerformanceController(PerformanceService performanceService, CourseRepository courseRepository, UserService userService) {
        this.performanceService = performanceService;
        this.courseRepository = courseRepository;
        this.userService = userService;
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'INSTRUCTOR')")
    @GetMapping("/grades")
    public ResponseEntity<ByteArrayResource> getGradesReport(@PathVariable Long courseId, Principal principal) {
        User currentUser = userService.getUser(principal);
        Course course = getCourseOrThrow(courseId);

        if (currentUser.getRole() == Role.INSTRUCTOR && currentUser.getId() != course.getInstructor().getId()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }

        byte[] report = performanceService.generateGradesExcelReport(courseId);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=grades_report.xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                .body(new ByteArrayResource(report));
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'INSTRUCTOR')")
    @GetMapping("/attendance")
    public ResponseEntity<ByteArrayResource> getAttendanceReport(@PathVariable Long courseId, Principal principal) {
        User currentUser = userService.getUser(principal);
        Course course = getCourseOrThrow(courseId);

        if (currentUser.getRole() == Role.INSTRUCTOR && currentUser.getId() != course.getInstructor().getId()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }

        byte[] report = performanceService.generateAttendanceExcelReport(courseId);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=attendance_report.xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                .body(new ByteArrayResource(report));
    }

    private Course getCourseOrThrow(Long courseId) {
        return courseRepository.findById(courseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found"));
    }
}
