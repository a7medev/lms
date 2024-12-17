package com.lms.course.post;

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
}
