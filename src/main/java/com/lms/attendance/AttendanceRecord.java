package com.lms.attendance;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.lms.course.Course;
import com.lms.course.lesson.Lesson;
import com.lms.user.User;
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

    @JsonIgnore
    @ManyToOne
    @MapsId("courseId")
    @JoinColumn(name = "course_id", referencedColumnName = "courseId")
    private Course course;

    @JsonIgnore
    @ManyToOne
    @MapsId("lessonId")
    @JoinColumn(name = "lesson_id", referencedColumnName = "id")
    private Lesson lesson;

    @JsonIgnore
    @ManyToOne
    @MapsId("studentId")
    @JoinColumn(name = "student_id", referencedColumnName = "id")
    private User student;

    private boolean attended;

    @CreatedDate
    private LocalDateTime timestamp;
}
