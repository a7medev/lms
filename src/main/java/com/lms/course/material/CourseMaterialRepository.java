package com.lms.course.material;


import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface CourseMaterialRepository extends JpaRepository<CourseMaterial, Long> {
    List<CourseMaterial> findAllByPostCourseUpdateId(Long postId);

    Optional<CourseMaterial> findByPostCourseUpdateIdAndMaterialId(Long postId, Long materialId);
}