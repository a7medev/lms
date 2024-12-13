package com.lms.assignment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AssignmentRepository extends JpaRepository<Assignment, Long> {

    List<Assignment> findAllByCourseId(Long courseId);

    List<Assignment> findAllByCourseIdAndDueDateAfter(Long courseId, LocalDateTime dueDate);

}
