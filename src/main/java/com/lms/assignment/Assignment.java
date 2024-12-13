package com.lms.assignment;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.lms.assignment.submission.AssignmentSubmission;
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
public class Assignment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // TODO: Switch to relation with course once Course entity is added.
    @NotNull private Long courseId;
    @NotNull private String title;
    @NotNull private String description;
    @NotNull private LocalDateTime dueDate;

    @JsonIgnore
    @OneToMany(mappedBy = "assignment")
    private Collection<AssignmentSubmission> submissions;
}
