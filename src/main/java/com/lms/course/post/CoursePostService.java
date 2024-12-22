package com.lms.course.post;

import com.lms.course.Course;
import com.lms.course.CourseService;
import com.lms.enrollment.EnrollmentRepository;
import com.lms.user.User;
import com.lms.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.lms.util.AuthUtils.principalToUser;

@RequiredArgsConstructor
@Service
public class CoursePostService {

    private final CoursePostRepository coursePostRepository;
    private final UserRepository userRepository;
    private final EnrollmentRepository enrollmentRepository;
    public List<CoursePost> getPosts(long courseId) {
        return coursePostRepository.findAllByCourseCourseId(courseId);
    }

    public CoursePost createPost(CoursePost coursePost) {
        return coursePostRepository.save(coursePost);
    }

    public Optional<CoursePost> getPostById(Long courseId, Long postId) {
        return coursePostRepository.findByCourseCourseIdAndCourseUpdateId(courseId,postId);
    }

    public void deletePost(Long postId) {
        coursePostRepository.deleteById(postId);
    }

    public List<CoursePost> getPostsForCurrentStudent(Principal currentUser) {
        User user = principalToUser(currentUser);

        List<Course> enrolledCourses = enrollmentRepository.findAllByUser(user).stream()
                .map(enrollment -> enrollment.getCourse())
                .toList();

        return enrolledCourses.stream()
                .flatMap(course -> coursePostRepository.findAllByCourse(course).stream())
                .collect(Collectors.toList());
    }
}
