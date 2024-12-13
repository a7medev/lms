package com.lms.assignment.submission;

import com.lms.assignment.Assignment;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class AssignmentSubmission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "assignment_id", nullable = false)
    private Assignment assignment;

    // TODO: replace with actual relationship when Course and User entities exist.
    @NotNull private Long studentId;

    @NotNull private String fileLocation;
    @CreatedDate private LocalDateTime submittedAt;
    @Null private Integer score;

}
