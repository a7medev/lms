package com.lms.attendance;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "attendance_record", uniqueConstraints = @UniqueConstraint(columnNames = {"course_id", "lesson_id", "student_id"}))
public class AttendanceRecord {

    @EmbeddedId
    private AttendanceKey id;  

    private boolean attended;
    private LocalDateTime timestamp;

    public AttendanceKey getId() { return id; }
    public void setId(AttendanceKey id) { this.id = id; }

    public boolean isAttended() { return attended; }
    public void setAttended(boolean attended) { this.attended = attended; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
