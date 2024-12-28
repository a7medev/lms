package com.lms.util;

import com.lms.course.Course;
import com.lms.enrollment.EnrollmentRepository;
import com.lms.enrollment.EnrollmentState;
import com.lms.user.User;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.security.Principal;
import java.util.Objects;

public class AuthUtils {

    public static User principalToUser(Principal principal) {
        return (User) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();
    }
    public static boolean hasCourseAccess(Course course, User user, EnrollmentRepository enrollmentRepository) {
        return switch (user.getRole()) {
            case STUDENT -> enrollmentRepository.existsByCourseCourseIdAndUserIdAndEnrollmentState(course.getCourseId(), user.getId(), EnrollmentState.ACTIVE);
            case INSTRUCTOR -> Objects.equals(course.getInstructor().getId(), user.getId());
            case ADMIN -> true;
        };
    }
}
