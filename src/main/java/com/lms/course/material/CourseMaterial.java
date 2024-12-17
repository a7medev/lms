package com.lms.course.material;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.lms.course.post.CoursePost;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)

@Table

public class CourseMaterial {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long materialId;

    // reference to post
    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "course_update_id", nullable = false)
    private CoursePost post;

    @NotNull
    private String fileLocation;
    @NotNull String contentType;
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
