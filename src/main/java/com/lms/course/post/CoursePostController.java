package com.lms.course.post;

import com.lms.course.Course;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping(path = "courses/{courseId}/posts")
public class CoursePostController {

    private final CoursePostService coursePostService;

    @Autowired
    public CoursePostController(CoursePostService coursePostService) {
        this.coursePostService = coursePostService;
    }

    @GetMapping
    public List<CoursePost> getAllPosts(@PathVariable Long courseId) {
            return coursePostService.getAllPosts();
    }

    @PostMapping
    public ResponseEntity<CoursePost> createPost(@RequestBody CoursePost coursePost, @PathVariable Long courseId) {
        Course course = new Course();
        course.setCourseId(courseId);
        coursePost.setCourse(course);
        CoursePost createdPost = coursePostService.createPost(coursePost,courseId);
        return new ResponseEntity<>(createdPost, HttpStatus.CREATED);
    }

    @GetMapping("/{postId}")
    public CoursePost getPostById(@PathVariable Long courseId ,@PathVariable Long postId) {
        return coursePostService.getPostById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));
    }

    @DeleteMapping("/{postId}")
    public void deletePost(@PathVariable Long courseId, @PathVariable Long postId) {
        coursePostService.deletePost(postId);
    }
}
