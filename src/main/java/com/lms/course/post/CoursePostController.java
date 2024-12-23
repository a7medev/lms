package com.lms.course.post;

import com.lms.course.Course;
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
import java.util.Optional;

import static com.lms.util.AuthUtils.principalToUser;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "courses/{courseId}/posts")
public class CoursePostController {
    private final CoursePostService coursePostService;


    @PreAuthorize("hasAnyAuthority('STUDENT', 'INSTRUCTOR', 'ADMIN')")
    @GetMapping
    public List<CoursePost> getPostsForCurrentUser(@PathVariable Long courseId, Principal principal) {
        User user = principalToUser(principal);
        return coursePostService.getPostsForCurrentUser(courseId, user);
    }

//    @PreAuthorize("hasAuthority('ADMIN')")
//    @GetMapping
//    public List<CoursePost> getPosts(@PathVariable Long courseId) {
//            return coursePostService.getPosts(courseId);
//    }

    @PreAuthorize("hasAnyAuthority('ADMIN','INSTRUCTOR')")
    @PostMapping
    public ResponseEntity<CoursePost> createPost(@RequestBody CoursePost coursePost, @PathVariable Long courseId, Principal principal) {
        CoursePost createdPost = coursePostService.createPost(coursePost,courseId,principal);
        return new ResponseEntity<>(createdPost, HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyAuthority('STUDENT', 'INSTRUCTOR', 'ADMIN')")
    @GetMapping("/{postId}")
    public CoursePost getPostByIdForCurrentUser(@PathVariable Long courseId, @PathVariable Long postId, Principal principal) {
        User user = principalToUser(principal);
        return coursePostService.getPostByIdForCurrentUser(courseId, postId, user)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','INSTRUCTOR')")
    @DeleteMapping("/{postId}")
    public void deletePost(@PathVariable Long postId) {
        coursePostService.deletePost(postId);
    }
}
