package com.lms.course.lesson;

import com.lms.course.Course;
import com.lms.course.CourseService;
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
@RequestMapping(path = "courses/{courseId}/lessons")
public class LessonController {

    private final LessonService lessonService;
    private final CourseService courseService;

    @Autowired
    public LessonController(LessonService lessonService, CourseService courseService) {
        this.lessonService = lessonService;
        this.courseService = courseService;
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('INSTRUCTOR')")
    public ResponseEntity<Lesson> createLesson(@RequestBody Lesson lesson, @PathVariable Long courseId, Principal principal) {
        User user = principalToUser(principal);
        Course course = courseService.getCourseById(courseId)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found"));

        lesson.setCourse(course);

        Lesson createdLesson = lessonService.createLesson(lesson, user);

        return new ResponseEntity<>(createdLesson, HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'INSTRUCTOR', 'STUDENT')")
    @GetMapping
    public List<Lesson> getLessons(@PathVariable Long courseId, Principal principal) {
        User user = principalToUser(principal);
        return lessonService.getLessons(courseId, user);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'INSTRUCTOR', 'STUDENT')")
    @GetMapping("/{lessonId}")
    public Lesson getLessonById(@PathVariable Long courseId , @PathVariable Long lessonId, Principal principal) {
        User user = principalToUser(principal);
        return lessonService.getLessonById(courseId, lessonId, user);
    }

}
