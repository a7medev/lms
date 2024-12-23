package com.lms.course.lesson;

import com.lms.course.Course;
import com.lms.course.CourseRepository;
import com.lms.enrollment.EnrollmentRepository;
import com.lms.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static com.lms.util.AuthUtils.hasCourseAccess;


@Service
public class LessonService {

    private final LessonRepository lessonRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;

    @Autowired
    public LessonService(LessonRepository lessonRepository, EnrollmentRepository enrollmentRepository, CourseRepository courseRepository) {
        this.lessonRepository = lessonRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.courseRepository = courseRepository;
    }

    public List<Lesson> getLessons(long courseId, User user) {
        Course course = courseRepository.findByCourseId(courseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found"));

        if (!hasCourseAccess(course, user, enrollmentRepository)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }

        return lessonRepository.findAllByCourseCourseId(courseId);
    }

    public Lesson createLesson(Lesson lesson, User user) {
        if (user.getId() != lesson.getCourse().getInstructor().getId()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }

        return lessonRepository.save(lesson);
    }

    public Lesson getLessonById(Long courseId, Long lessonId, User user) {
        Lesson lesson = lessonRepository.findByCourseCourseIdAndId(courseId, lessonId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lesson not found"));

        if (!hasCourseAccess(lesson.getCourse(), user, enrollmentRepository)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }

        return lesson;
    }
}
