package com.lms.assignment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping(path = "/courses/{courseId}/assignments")
public class AssignmentController {

    private final AssignmentService assignmentService;

    @Autowired
    public AssignmentController(AssignmentService assignmentService) {
        this.assignmentService = assignmentService;
    }

    @GetMapping
    public List<Assignment> getAssignments(
            @PathVariable Long courseId,
            @RequestParam(defaultValue = "false") Boolean upcoming
    ) {
        System.out.println(courseId);
        System.out.println(upcoming);
        return assignmentService.getAssignments(courseId, upcoming);
    }

    @GetMapping("{assignmentId}")
    public Assignment getAssignment(@PathVariable Long courseId, @PathVariable Long assignmentId) {
        return assignmentService.getAssignment(assignmentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Assignment not found"));
    }

    @PostMapping
    public Assignment createAssignment(@PathVariable Long courseId, @RequestBody Assignment assignment) {
        assignment.setCourseId(courseId);
        return assignmentService.createAssignment(assignment);
    }

}
