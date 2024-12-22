package com.lms.course;

import com.lms.enrollment.EnrollmentRepository;
import com.lms.user.Role;
import com.lms.user.User;
import lombok.RequiredArgsConstructor;
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

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public Course createCourse(Course course) {
        return courseRepository.save(course);
    }

    public Optional<Course> getCourseById(Long courseId) {
        return courseRepository.findByCourseId(courseId);
    }


    public void deleteCourse(Long courseId) {
        courseRepository.deleteById(courseId);
    }
//
//    public List<Course> getCoursesForCurrentUser(Principal currentUser) {
//        User user = principalToUser(currentUser);
//
//        if (user.getRole() == Role.ADMIN) {
//            return courseRepository.findAll();
//        } else if (user.getRole() == Role.INSTRUCTOR) {
//            return courseRepository.findAllByUser(user);
//        } else if (user.getRole() == Role.STUDENT) {
//            return enrollmentRepository.findAllByUser(user).stream()
//                    .map(enrollment -> enrollment.getCourse())
//                    .collect(Collectors.toList());
//        } else {
//            throw new IllegalStateException("Unauthorized role detected");
//        }
//    }
}
