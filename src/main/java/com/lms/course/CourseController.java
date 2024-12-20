package com.lms.course;

import com.lms.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;

import static com.lms.util.AuthUtils.principalToUser;

@RestController
@RequestMapping("/courses")
public class CourseController {
    CourseService courseService;

    @Autowired
    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping
    public List<Course> getAllCourses() {
        return courseService.getAllCourses();
    }

    @PreAuthorize("hasAnyAuthority('INSTRUCTOR', 'ADMIN')")
    @PostMapping
    public ResponseEntity<Course> createCourse(@RequestBody Course course, Principal principal) {
        User user = principalToUser(principal);
        course.setInstructor(user);

        Course createdCourse = courseService.createCourse(course);
        return new ResponseEntity<>(createdCourse, HttpStatus.CREATED);
    }

    @GetMapping("/{courseId}")
    public Course getCourseById(@PathVariable Long courseId) {
        return courseService.getCourseById(courseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found"));
    }


    @DeleteMapping("/{courseId}")
    public void deleteCourse(@PathVariable Long courseId) {
        courseService.deleteCourse(courseId);
    }
}
