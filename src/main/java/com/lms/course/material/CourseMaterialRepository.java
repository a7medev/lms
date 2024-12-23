package com.lms.course.material;


import com.lms.course.post.CoursePost;
import com.lms.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

public interface CourseMaterialRepository extends JpaRepository<CourseMaterial, Long> {
    List<CourseMaterial> findAllByPost_CourseCourseIdAndPost_CourseUpdateId(Long courseId, Long courseUpdateId);
    List<CourseMaterial> findAllByPost_Course_Instructor(User instructor);
    Optional<CourseMaterial> findByPostCourseUpdateIdAndMaterialId(Long postId, Long materialId);
    CourseMaterial findByMaterialIdAndPost_Course_Instructor(Long id, User instructor);
    Optional<CourseMaterial> findByMaterialIdAndPost_CourseCourseIdAndPost_CourseUpdateId(Long id, Long courseId, Long courseUpdateId);
    boolean existsByPost_Course_CourseIdAndPost_Course_Instructor(Long courseId, User instructor);
}