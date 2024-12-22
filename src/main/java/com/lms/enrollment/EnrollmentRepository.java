package com.lms.enrollment;

import com.lms.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface EnrollmentRepository extends JpaRepository<Enrollment,Long>{
    List<Enrollment> findAllByCourseCourseId(Long courseId);
<<<<<<< HEAD
    List<Enrollment> findAllByCourseCourseIdAndEnrollmentState(Long courseId, EnrollmentState state);
=======
    Collection<Enrollment> findAllByUser(User user);
>>>>>>> 0b12282 (fix: show course details based on user's role)
}
