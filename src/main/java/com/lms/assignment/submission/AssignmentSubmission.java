package com.lms.assignment.submission;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.lms.assignment.Assignment;
import com.lms.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table
@EntityListeners(AuditingEntityListener.class)
public class AssignmentSubmission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "assignment_id", nullable = false)
    private Assignment assignment;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @NotNull private String contentType;
    @NotNull private String fileLocation;
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime submittedAt;
    @Null private Integer score;

}
