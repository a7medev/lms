package com.lms.course.post;

import com.lms.course.Course;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class CoursePostService {

    private final CoursePostRepository coursePostRepository;

    @Autowired
    public CoursePostService(CoursePostRepository coursePostRepository) {
        this.coursePostRepository = coursePostRepository;
    }

    public List<CoursePost> getAllPosts() {
        return coursePostRepository.findAll();
    }

    public CoursePost createPost(CoursePost coursePost, Long courseId) {
        return coursePostRepository.save(coursePost);
    }

    public Optional<CoursePost> getPostById(Long postId) {
        return coursePostRepository.findByCourseUpdateId(postId);
    }

    public void deletePost(Long postId) {
        coursePostRepository.deleteById(postId);
    }
}
