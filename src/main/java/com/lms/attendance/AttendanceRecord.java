package com.lms.attendance;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "attendance_record", uniqueConstraints = @UniqueConstraint(columnNames = {"course_id", "lesson_id", "student_id"}))
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceRecord {

    @EmbeddedId
    private AttendanceKey id;

    private boolean attended;

    @CreatedDate
    private LocalDateTime timestamp;
}
