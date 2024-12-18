package com.lms.assignment;

import com.lms.course.Course;
import com.lms.course.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

// TODO: When enrollment is implemented, allow only enrolled students to access course materials.
// TODO: Allow only instructors for the specific courses to publish course material and view the course details.

@RestController
@RequestMapping(path = "/courses/{courseId}/assignments")
public class AssignmentController {

    private final AssignmentService assignmentService;
    private final CourseService courseService;

    @Autowired
    public AssignmentController(AssignmentService assignmentService, CourseService courseService) {
        this.assignmentService = assignmentService;
        this.courseService = courseService;
    }

    @GetMapping
    public List<Assignment> getAssignments(
            @PathVariable Long courseId,
            @RequestParam(defaultValue = "false") Boolean upcoming
    ) {
        return assignmentService.getAssignments(courseId, upcoming);
    }

    @GetMapping("{assignmentId}")
    public Assignment getAssignment(@PathVariable Long courseId, @PathVariable Long assignmentId) {
        return assignmentService.getAssignment(assignmentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Assignment not found"));
    }

    @PreAuthorize("hasAuthority('INSTRUCTOR')")
    @PostMapping
    public Assignment createAssignment(@PathVariable Long courseId, @RequestBody Assignment assignment) {
        Course course = courseService.getCourseById(courseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found"));

        assignment.setCourse(course);

        return assignmentService.createAssignment(assignment);
    }

}
