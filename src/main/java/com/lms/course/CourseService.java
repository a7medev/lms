package com.lms.course;

import com.lms.enrollment.Enrollment;
import com.lms.enrollment.EnrollmentRepository;
import com.lms.user.Role;
import com.lms.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import static com.lms.util.AuthUtils.principalToUser;

@RequiredArgsConstructor
@Service
public class CourseService {
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;

    // Based on user role, fetch specific courses
    public List<Course> getCoursesForCurrentUser(User user) {
        if (user.getRole() == Role.ADMIN) {
            return courseRepository.findAll();
        } else if (user.getRole() == Role.INSTRUCTOR) {
            return courseRepository.findAllByInstructor(user);
        } else if (user.getRole() == Role.STUDENT) {
            return getCoursesForStudent(user);
        } else {
            throw new IllegalStateException("Unauthorized role");
        }
    }

    public List<Course> getCoursesForStudent(User user) {
        return enrollmentRepository.findAllByUser(user).stream()
                .map(Enrollment::getCourse)
                .collect(Collectors.toList());
    }

    public Course createCourse(Course course) {
        return courseRepository.save(course);
    }
    public Optional<Course> getCourseById(Long courseId){
        return courseRepository.findById(courseId);
    }

    public Course getCourseByIdForCurrentUser(Long courseId, User user) {
        if (user.getRole() == Role.ADMIN) {
            return courseRepository.findByCourseId(courseId)
                    .orElseThrow(() -> new IllegalStateException("Course not found"));
        } else if (user.getRole() == Role.INSTRUCTOR) {
            Course course = courseRepository.findByCourseId(courseId)
                    .orElseThrow(() -> new IllegalStateException("Course not found"));
            if (course.getInstructor().getId() != user.getId()) {
                throw new IllegalStateException("Instructor is not associated with this course");
            }

            return course;
        } else if (user.getRole() == Role.STUDENT) {
            Optional<Course> course = enrollmentRepository.findAllByUser(user).stream()
                    .map(Enrollment::getCourse)
                    .filter(c -> c.getCourseId().equals(courseId))
                    .findFirst();

            return course.orElseThrow(() -> new IllegalStateException("Course not found or not enrolled"));
        } else {
            throw new IllegalStateException("Unauthorized role detected");
        }
    }


    public void deleteCourse(Long courseId) {
        courseRepository.deleteById(courseId);
    }
}
