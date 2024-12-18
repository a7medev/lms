package com.lms.assignment;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.lms.assignment.submission.AssignmentSubmission;
import com.lms.course.Course;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Collection;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table
public class Assignment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @NotNull private String title;
    @NotNull private String description;
    @NotNull private LocalDateTime dueDate;

    @JsonIgnore
    @OneToMany(mappedBy = "assignment")
    private Collection<AssignmentSubmission> submissions;
}
