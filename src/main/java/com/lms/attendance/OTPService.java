package com.lms.attendance;

import com.lms.course.Course;
import com.lms.course.CourseRepository;
import com.lms.enrollment.EnrollmentRepository;
import com.lms.user.User;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static com.lms.util.AuthUtils.hasCourseAccess;

@Service
public class OTPService {

    private final Map<String, String> otpStorage = new HashMap<>();
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;

    public OTPService(CourseRepository courseRepository, EnrollmentRepository enrollmentRepository) {
        this.courseRepository = courseRepository;
        this.enrollmentRepository = enrollmentRepository;
    }

    public String generateOtp(Long courseId, Long lessonId, User user) {
        Course course = courseRepository.findByCourseId(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found"));

        if (!hasCourseAccess(course, user, enrollmentRepository)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }

        String otp = String.valueOf(new Random().nextInt(999999));
        otpStorage.put(courseId + "_" + lessonId, otp);
        return otp;
    }

    public String getOtp(Long courseId, Long lessonId) {
        return otpStorage.get(courseId + "_" + lessonId);
    }
}
