package com.lms.course;

import com.lms.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;

import static com.lms.util.AuthUtils.principalToUser;
@RequiredArgsConstructor
@RestController
@RequestMapping("/courses")
public class CourseController {
    @Autowired
    private final CourseService courseService;

    @PreAuthorize("hasAnyAuthority('STUDENT', 'INSTRUCTOR', 'ADMIN')")
    @GetMapping
    public List<Course> getCoursesForCurrentUser(Principal principal) {
        User user = principalToUser(principal);
        return courseService.getCoursesForCurrentUser(user);
    }

    @PreAuthorize("hasAnyAuthority('INSTRUCTOR', 'ADMIN')")
    @PostMapping
    public ResponseEntity<Course> createCourse(@RequestBody Course course, Principal principal) {
        User user = principalToUser(principal);
        course.setInstructor(user);

        Course createdCourse = courseService.createCourse(course);
        return new ResponseEntity<>(createdCourse, HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyAuthority('STUDENT', 'INSTRUCTOR', 'ADMIN')")
    @GetMapping("/{courseId}")
    public Course getCourseById(@PathVariable Long courseId, Principal principal) {
        User user = principalToUser(principal);
        return courseService.getCourseByIdForCurrentUser(courseId, user);
    }


    @PreAuthorize("hasAnyAuthority('INSTRUCTOR', 'ADMIN')")
    @DeleteMapping("/{courseId}")
    public ResponseEntity<Void> deleteCourse(@PathVariable Long courseId, Principal principal) {
        User user = principalToUser(principal);
        courseService.deleteCourse(courseId, user);
        return ResponseEntity.noContent().build();
    }
}
