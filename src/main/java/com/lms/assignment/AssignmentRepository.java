package com.lms.assignment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AssignmentRepository extends JpaRepository<Assignment, Long> {

    List<Assignment> findAllByCourseCourseId(Long courseId);

    List<Assignment> findAllByCourseCourseIdAndDueDateAfter(Long courseId, LocalDateTime dueDate);

}
