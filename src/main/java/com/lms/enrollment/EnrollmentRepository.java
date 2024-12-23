package com.lms.enrollment;

import com.lms.course.Course;
import com.lms.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface EnrollmentRepository extends JpaRepository<Enrollment,Long>{
    List<Enrollment> findAllByCourseCourseId(Long courseId);
    List<Enrollment> findAllByCourseCourseIdAndEnrollmentState(Long courseId, EnrollmentState state);
    Collection<Enrollment> findAllByUser(User user);
    @Query("SELECT e.course FROM Enrollment e WHERE e.user = :user AND e.course.courseId = :courseId")
    Optional<Course> findCourseByUserAndCourseId(@Param("user") User user, @Param("courseId") Long courseId);
    @Query("SELECT e.course FROM Enrollment e WHERE e.user = :user")
    List<Course> findCoursesByUser(@Param("user") User user);

}
