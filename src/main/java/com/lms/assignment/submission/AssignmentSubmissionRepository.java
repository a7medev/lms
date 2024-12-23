package com.lms.assignment.submission;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface AssignmentSubmissionRepository extends JpaRepository<AssignmentSubmission, Long> {
    List<AssignmentSubmission> findAllByAssignmentId(Long assignmentId);
    boolean existsByAssignmentIdAndStudentId(Long assignmentId, int studentId);
}
