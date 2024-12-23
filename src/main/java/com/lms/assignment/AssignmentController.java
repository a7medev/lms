package com.lms.assignment;

import com.lms.course.Course;
import com.lms.course.CourseService;
import com.lms.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;

import static com.lms.util.AuthUtils.principalToUser;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/courses/{courseId}/assignments")
public class AssignmentController {

    private final AssignmentService assignmentService;
    private final CourseService courseService;

    @GetMapping
    public List<Assignment> getAssignments(
            @PathVariable Long courseId,
            @RequestParam(defaultValue = "false") Boolean upcoming,
            Principal principal
    ) {
        User user = principalToUser(principal);
        return assignmentService.getAssignments(courseId, user, upcoming);
    }

    @GetMapping("{assignmentId}")
    public Assignment getAssignment(@PathVariable Long courseId, @PathVariable Long assignmentId, Principal principal) {
        User user = principalToUser(principal);
        return assignmentService.getAssignment(assignmentId, user);
    }

    @PreAuthorize("hasAuthority('INSTRUCTOR')")
    @PostMapping
    public Assignment createAssignment(@PathVariable Long courseId, @RequestBody Assignment assignment, Principal principal) {
        Course course = courseService.getCourseById(courseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found"));

        assignment.setCourse(course);

        User user = principalToUser(principal);

        return assignmentService.createAssignment(assignment, user);
    }

}
