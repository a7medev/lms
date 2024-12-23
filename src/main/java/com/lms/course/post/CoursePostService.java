package com.lms.course.post;

import com.lms.course.Course;
import com.lms.course.CourseRepository;
import com.lms.course.CourseService;
import com.lms.user.Role;
import com.lms.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.lms.util.AuthUtils.principalToUser;

@RequiredArgsConstructor
@Service
public class CoursePostService {

    private final CoursePostRepository coursePostRepository;
    private final CourseService courseService;
    private final CourseRepository courseRepository;

    public List<CoursePost> getPostsForCurrentUser(Long courseId, User user) {
        Course course = courseService.getCourseByIdForCurrentUser(courseId, user);
        return course.getPosts();
    }

    public Optional<CoursePost> getPostByIdForCurrentUser(Long courseId, Long postId, User user) {
        Course course = courseService.getCourseByIdForCurrentUser(courseId, user);

        return course.getPosts().stream()
                .filter(post -> post.getCourseUpdateId().equals(postId))
                .findFirst();
    }

    public CoursePost createPost(CoursePost coursePost, Long courseId, Principal principal) {
        User user = principalToUser(principal);

        if (user.getRole() == Role.INSTRUCTOR) {
            boolean isInstructorForCourse = courseRepository.existsByCourseIdAndInstructor(courseId, user);
            if (!isInstructorForCourse) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not the instructor for this course");
            }
        }

        Course course = new Course();
        course.setCourseId(courseId);
        coursePost.setCourse(course);
        return coursePostRepository.save(coursePost);
    }

    public void deletePost(Long courseId, Long postId, Principal principal) {
        User user = principalToUser(principal);

        if (user.getRole() == Role.INSTRUCTOR) {
            boolean isInstructorForCourse = courseRepository.existsByCourseIdAndInstructor(courseId, user);
            if (!isInstructorForCourse) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not the instructor for this course");
            }
        }

        coursePostRepository.deleteById(postId);
    }


}
