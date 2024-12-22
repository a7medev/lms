package com.lms.course.post;

import com.lms.course.Course;
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
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "courses/{courseId}/posts")
public class CoursePostController {

    private final CoursePostService coursePostService;


    @GetMapping
    public List<CoursePost> getPostsForCurrentUser(Principal currentUser){
        return coursePostService.getPostsForCurrentStudent(currentUser);
    }
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping
    public List<CoursePost> getPosts(@PathVariable Long courseId) {
            return coursePostService.getPosts(courseId);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','INSTRUCTOR')")
    @PostMapping
    public ResponseEntity<CoursePost> createPost(@RequestBody CoursePost coursePost, @PathVariable Long courseId) {
        Course course = new Course();
        course.setCourseId(courseId);
        coursePost.setCourse(course);
        CoursePost createdPost = coursePostService.createPost(coursePost);
        return new ResponseEntity<>(createdPost, HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','INSTRUCTOR')")
    @GetMapping("/{postId}")
    public CoursePost getPostById(@PathVariable Long courseId ,@PathVariable Long postId) {
        return coursePostService.getPostById(courseId,postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','INSTRUCTOR')")
    @DeleteMapping("/{postId}")
    public void deletePost(@PathVariable Long postId) {
        coursePostService.deletePost(postId);
    }
}
