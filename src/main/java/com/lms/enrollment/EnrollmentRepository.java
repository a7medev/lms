package com.lms.enrollment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EnrollmentRepository extends JpaRepository<Enrollment,Long>{
    List<Enrollment> findAllByCourseCourseId(Long courseId);
    List<Enrollment> findAllByCourseCourseIdAndEnrollmentState(Long courseId, EnrollmentState state);
    boolean existsByCourseCourseIdAndUserIdAndEnrollmentState(Long courseId, int userId, EnrollmentState enrollmentState);
}
