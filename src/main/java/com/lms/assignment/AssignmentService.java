package com.lms.assignment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AssignmentService {
    final AssignmentRepository assignmentRepository;

    @Autowired
    public AssignmentService(AssignmentRepository assignmentRepository) {
        this.assignmentRepository = assignmentRepository;
    }

    public List<Assignment> getAssignments(Long courseId, boolean upcoming) {
        if (upcoming) {
            LocalDateTime now = LocalDateTime.now();
            return assignmentRepository.findAllByCourseCourseIdAndDueDateAfter(courseId, now);
        }
        return assignmentRepository.findAllByCourseCourseId(courseId);
    }

    public Optional<Assignment> getAssignment(Long assignmentId) {
        return assignmentRepository.findById(assignmentId);
    }

    public Assignment createAssignment(Assignment assignment) {
        return assignmentRepository.save(assignment);
    }

}
