package com.lms.course.post;
import com.lms.course.Course;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CoursePostRepository extends JpaRepository<CoursePost, Long> {
    Optional<CoursePost> findByCourseUpdateId(Long courseUpdateId);

}

